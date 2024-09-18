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

    private static int id = 1;

    private static File file = new File("src/main/resources/log.json");
    private static ObjectMapper mapper = new ObjectMapper();
    private static ArrayList<TaskList> list;

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
                break;
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
            if (slice[0].equals("list")) {
                if (slice.length > 1) {
                    showByStatus(slice[1]);
                    break;
                } else {
                    showAll();
                    break;
                }
            }
            if (slice[0].equals("update")) {
                updateTask(Integer.parseInt(slice[1]), slice[2]);
                break;
            }
            if (slice[0].equals("delete")) {
                deleteTask(Integer.parseInt(slice[1]));
                break;
            }
        }


    }

/*
 * 
 * GET
 * 
 */

    // show all tasks
    private static void showAll() throws IOException {
        for (TaskList task : TaskTracker.list)
            System.out.println(task.toString());
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

    private static void updateTask(int id, String descOrStatus) throws IOException {
        TaskList task = list.get(id - 1);
        
        if (descOrStatus.equalsIgnoreCase("TODO") || descOrStatus.equalsIgnoreCase("in-progress") || descOrStatus.equalsIgnoreCase("done")) {
            task.setStatus(descOrStatus);
            list.set(task.getId() - 1, task);
        } else {
            task.setDescription(descOrStatus);
            list.set(task.getId() - 1, task);
        }

        // SAVE JSON
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
        task.setId(TaskTracker.id++);
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
