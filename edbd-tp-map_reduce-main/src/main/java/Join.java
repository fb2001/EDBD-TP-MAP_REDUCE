import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Join {
    private static final String INPUT_PATH = "input-join/";
    private static final String OUTPUT_PATH = "output/join-";
    private static final Logger LOG = Logger.getLogger(Join.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n%6$s");

        try {
            FileHandler fh = new FileHandler("join.log");
            fh.setFormatter(new SimpleFormatter());
            LOG.addHandler(fh);
        } catch (SecurityException | IOException e) {
            System.exit(1);
        }
    }

    public static class Map extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Context context) {
            // TODO à compléter
            String line = value.toString();
            String[] tokens = line.split("\\|");

            try {
                if (tokens.length < 2) {
                    return;
                }

                if (tokens.length == 8) {
                    // System.out.println("CUSTOMER: " + tokens[0] + " " + tokens[1]);
                    String customerId = tokens[0].trim();
                    String customerName = tokens[1].trim();
                    context.write(new Text(customerId), new Text("CUSTOMER:" + customerName));
                }

                if (tokens.length == 9) {
                    // System.out.println("ORDER: " + tokens[1] + " " + tokens[8]);
                    String orderCustomerId = tokens[1].trim();
                    String orderComment = tokens[8].trim();
                    context.write(new Text(orderCustomerId), new Text("ORDER:" + orderComment));
                }

            } catch (IOException | InterruptedException e) {
                LOG.severe(e.getMessage());
            }
        }
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // TODO à compléter
            List<String> customers = new ArrayList<>();
            List<String> orders = new ArrayList<>();

            for (Text value : values) {
                String val = value.toString();
                if (val.startsWith("CUSTOMER:")) {
                    customers.add(val.substring(9));
                } else if (val.startsWith("ORDER:")) {
                    orders.add(val.substring(6));
                }
            }

            for (String customer : customers) {
                for (String order : orders) {
                    context.write(new Text(customer), new Text(order));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Join");
        job.setJarByClass(Join.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(INPUT_PATH));
        FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH + Instant.now().getEpochSecond()));

        job.waitForCompletion(true);
    }
}