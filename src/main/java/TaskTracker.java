import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class TaskTracker {

    private static int id;

    private static File file = new File("src/main/resources/log.json");
    private static ObjectMapper mapper = new ObjectMapper();
    private static ArrayList<TaskList> list;

    

    static {
        try {
            list = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, TaskList.class));
            id = list.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TaskTracker() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("test cli");

        while (true) {
            Scanner console = new Scanner(System.in);
            String command = console.nextLine();
            String[] slice = command.split(" ");
            if (slice[0].equals("create")) {
                String update = command.substring(command.indexOf("\"") + 1, command.lastIndexOf("\""));
                createTask(update, slice[2]);
            }
            else if (command.startsWith("mark")) {
                markTask(command, Integer.parseInt(slice[1]));
            }
            /*
             * list - all  tasks
             * 
             * 
             * tasks by status
             * 
             * list done
             * list todo
             * list in-progress
             * 
             */
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
            } else if (command.equals("exit")) {
                break;
            }
        }

        System.out.println("exit cli");
    }

/*
 * 
 * GET
 * 
 */

    // show all tasks
    private static void showAll() throws IOException {
        for (TaskList task : TaskTracker.list){
            System.out.println(task.toString());
        }
            
    }

    // show tasks by status
    private static void showByStatus(String status) {
        for (TaskList task : TaskTracker.list){
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
        TaskTracker.id--;
    }

/*
 * 
 * UPDATE
 * 
 */

    private static void updateTask(int id, String desc) throws IOException {
        TaskList task = list.get(id - 1);
        
        task.setDescription(desc);
        list.set(task.getId() - 1, task);

        // SAVE JSON
        saveJson();
    }

    private static void markTask(String command, int id) throws StreamWriteException, DatabindException, IOException {
        TaskList task = list.get(id - 1);
        if (command.contains("in-progress")) {
            task.setStatus("in-progress");
        }
        else if (command.contains("done")) {
            task.setStatus("done");
        } else {
            task.setStatus("todo");
        }
        list.set(task.getId() - 1, task);

        saveJson();
    }

/*
 * 
 * POST
 * 
 */

    // create new task
    private static void createTask(String description, String status) throws IOException {
        TaskList task = new TaskList();
        task.setDescription(description);
        task.setStatus(status);
        task.setId(TaskTracker.id + 1);
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

    static class TaskList {
        int id;
        String description;
        String status = "todo";
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
