import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GroupByCommande {

    private static final String INPUT_PATH = "input-groupBy/";
    private static final String OUTPUT_PATH = "output/groupBy-";
    private static final Logger LOG = Logger.getLogger(GroupByCommande.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n%6$s");

        try {
            FileHandler fh = new FileHandler("out.log");
            fh.setFormatter(new SimpleFormatter());
            LOG.addHandler(fh);
        } catch (SecurityException | IOException e) {
            System.exit(1);
        }
    }

    public static class Map extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // TODO: à compléter

            String[] fields = value.toString().split(",");

            String orderId = fields[1];  // Identifiant de la commande
            String productId = fields[13];  // identifiant de produit

            try {
                int quantity = Integer.parseInt(fields[18]);  // Quantity

                // normalement identifiant de la commande / produit / quantitéee
                context.write(new Text(orderId),
                        new Text(productId + ":" + quantity));
            } catch (NumberFormatException e) {
                LOG.severe("Invalid quantity value: " + fields[18]);
            }
        }
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            // TODO: à compléter


            HashSet<String> distinctProducts = new HashSet<>();
            int totalQuantity = 0;

            // Pour chaque valeur faire :
            for (Text val : values) {
                String[] productInfo = val.toString().split(":");

                // ajouter le produit dans la liste des produit distinct
                distinctProducts.add(productInfo[0]);

                // ajouter aussi ça quantité
                totalQuantity += Integer.parseInt(productInfo[1]);
            }

            // le texte pour l'ajouté a context write pour l'ajouté au fichier
            String output = String.format("Distinct Products: %d, Total Quantity: %d",
                    distinctProducts.size(), totalQuantity);

            context.write(key, new Text(output));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "GroupByCommande");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(GroupByCommande.Map.class);
        job.setReducerClass(GroupByCommande.Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(INPUT_PATH));
        FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH + Instant.now().getEpochSecond()));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
