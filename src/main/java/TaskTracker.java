import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.text.DateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TaskTracker {

    

    private static File file = new File("src/main/resources/log.json");
    private static ObjectMapper mapper = new ObjectMapper();
    private static ArrayList<Task> list;

    static {
        try {
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateFormat dateFormat = DateFormat.getInstance();
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // optional
            mapper.setDateFormat(dateFormat);
            list = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Task.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int id = list.size();

    public TaskTracker() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("test cli");

        String command = "";
        int word = 0;
        while (word < args.length) {
            Scanner sc = new Scanner(args[word]);
            if (args[0].equals("create")) {
                command += args[word] + " \"";
            } else if (args[0].equals("update")) {
                command += args[word] + " ";
                if (sc.hasNextInt()) { // следующее целое?
                    command += args[word + 1] + " ";
                    for (int i = word + 1; i < args.length; i++) { // update 1 "fdasfkasfha akf kasdhf"
                                                                   // |________|
                        if (i == word + 1) {
                            command += "\""; // update 1 "fdasfkasfha akf kasdhf"
                                             // |_________|
                            continue;
                        }

                        // TODO доделать!!!
                        
                    }
                }
            } 
            else {
                command += args[word] + " ";
            }
            
            word++;
        }

        System.out.println(command);

        String[] slice = command.split(" ");
        
        if (command.startsWith("create")) {
            String description = command.substring(command.indexOf("\"") + 1, command.lastIndexOf("\""));
            createTask(description);
        } else if (command.startsWith("mark")) {
            markTask(command, Integer.parseInt(slice[1]));
        }
        else if (slice[0].equals("list")) {
            if (slice.length > 1) {
                showByStatus(slice[1]);
            } else {
                showAll();
            }
        } else if (slice[0].equals("update")) {
            String update = command.substring(command.indexOf("\"") + 1, command.lastIndexOf("\""));
            updateTask(Integer.parseInt(slice[1]), update);
        } else if (slice[0].equals("delete")) {
            deleteTask(Integer.parseInt(slice[1]));
        }
    }
        

    /*
     * 
     * GET
     * 
     */

    // show all tasks
    private static void showAll() throws IOException {
        for (Task task : TaskTracker.list) {
            System.out.println(task.toString());
        }

    }

    // show tasks by status
    private static void showByStatus(String status) {
        for (Task task : TaskTracker.list) {
            if ((task.getStatus()).equalsIgnoreCase(status)) {
                System.out.println(task.toString());
            }
        }
    }

    /*
     * 
     * DELETE
     * 
     */

    // delete task
    private static void deleteTask(int id) throws IOException {
        list.remove(id - 1);
        // SAVE JSON
        saveJson();
        TaskTracker.id -= 1;
    }

    /*
     * 
     * UPDATE
     * 
     */

    private static void updateTask(int id, String desc) throws IOException {
        Task task = list.get(id - 1);

        task.setDescription(desc);
        task.setUpdatedAt();
        list.set(task.getId() - 1, task);

        // SAVE JSON
        saveJson();
    }

    private static void markTask(String command, int id) throws StreamWriteException, DatabindException, IOException {
        Task task = list.get(id - 1);
        if (command.contains("in-progress")) {
            task.setStatus("in-progress");
        } else if (command.contains("done")) {
            task.setStatus("done");
        } else {
            task.setStatus("todo");
        }
        task.setUpdatedAt();
        list.set(task.getId() - 1, task);

        saveJson();
    }

    /*
     * 
     * POST
     * 
     */

    // create new task
    private static void createTask(String description) throws IOException {
        Task task = new Task();
        task.setDescription(description);
        TaskTracker.id++;
        task.setId(TaskTracker.id);
        list.add(task);

        // SAVE JSON
        saveJson();
    }

    private static void saveJson() throws StreamWriteException, DatabindException, IOException {
        FileWriter fileWriter = new FileWriter(file);
        mapper.writeValue(fileWriter, list);
        fileWriter.close();
        System.out.println("JSON обновлён!");
    }

    static class Task {
        private int id;
        private String description;
        private String status = "todo";
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = 
        "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = 
        "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedAt;

        public Task() {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }

        @Override
        public String toString() {
            return "id: " + id + 
            "description: " + description + 
            "status: " + status +
            "createdAt: " + createdAt +
            "updatedAt: " + updatedAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt() {
            this.updatedAt = LocalDateTime.now();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
