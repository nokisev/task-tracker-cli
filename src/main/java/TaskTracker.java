import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class TaskTracker {

    private static int id = 3;

    private static File file = new File("src/main/resources/log.json");
    private static ObjectMapper mapper = new ObjectMapper();
    private static List<TaskList> list;

    static {
        try {
            list = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, TaskList.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TaskTracker() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("test cli");

        while (true) {
            Scanner command = new Scanner(System.in);
            String[] slice = (command.nextLine()).split(" ");
            if (slice[0].equals("create")) {
                createTask(slice[1], slice[2]);
            }
            if (slice[0].equals("list")) {
                showAll();
            }
            if (slice[0].equals("update")) {
                updateTask(Integer.parseInt(slice[1]), slice[2]);
            }
        }


    }

    // TODO: status
    private static void showDone () {

    }

    private static void showInProgress () {

    }

    private static void showTodo () {

    }

    // show all tasks
    private static void showAll() throws IOException {
        for (TaskList task : TaskTracker.list)
            System.out.println(task.toString());
    }

    private static void deleteTask(int id) {

    }

    private static void updateTask(int id, String des) {
        
    }

    // create new task
    private static void createTask(String description, String status) throws IOException {
        TaskList task = new TaskList();
        task.setDescription(description);
        task.setStatus(status);
        task.setId(TaskTracker.id++);
        list.add(task);
        FileWriter fileWriter = new FileWriter(file);
        mapper.writeValue(fileWriter, list);
        fileWriter.close();
        System.out.println("JSON обновлён!");
    }

    static class TaskList {
        int id;
        String description;
        String status;
//        DateFormat createdAt;
//        DateFormat updatedAt;


        @Override
        public String toString() {
            return  "id=" + id +
                    ", description='" + description + '\'' +
                    ", status='" + status;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

//        public DateFormat getCreatedAt() {
//            return createdAt;
//        }
//
//        public void setCreatedAt(DateFormat createdAt) {
//            this.createdAt = createdAt;
//        }
//
//        public DateFormat getUpdatedAt() {
//            return updatedAt;
//        }
//
//        public void setUpdatedAt(DateFormat updatedAt) {
//            this.updatedAt = updatedAt;
//        }

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
