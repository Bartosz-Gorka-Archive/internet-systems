package bartoszgorka;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShutdownHook extends Thread {
    /**
     * Hook with store collected statistics in CSV file
     */
    public void run() {
        File file = new File("statistics.csv");
        try {
            FileWriter fileWriter = new FileWriter(file);
            CSVWriter writer = new CSVWriter(fileWriter);

            // Add header
            String[] header = {"Domain", "Requests", "Bytes IN", "Bytes OUT"};
            writer.writeNext(header, false);

            // Group statistics by domain
            Map<String, List<Statistic>> collect = ProxyServer.getStatistics().stream()
                    .collect(Collectors.groupingBy(Statistic::getDomain, Collectors.toList()));
            for (Map.Entry<String, List<Statistic>> entries : collect.entrySet()) {
                ArrayList<String> response = new ArrayList<>();
                int receivedBytes = 0;
                int sentBytes = 0;
                int totalRequests = 0;

                // Sum transfers and calculate requests
                for (Statistic s : entries.getValue()) {
                    receivedBytes += s.getBytesReceived();
                    sentBytes += s.getBytesSent();
                    totalRequests += s.getRequests();
                }

                // Prepare response
                Statistic statistic = entries.getValue().get(0);
                response.add(statistic.getDomain());
                response.add(String.valueOf(totalRequests));
                response.add(String.valueOf(sentBytes));
                response.add(String.valueOf(receivedBytes));

                // Add response to CSV file
                writer.writeNext(response.toArray(new String[0]), false);
            }

            // Close writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
