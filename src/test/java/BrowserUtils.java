public class BrowserUtils {

    public static void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitFor(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getDownloadPath() {

        String userName = System.getProperty("user.name");
//        System.out.println("userName = " + userName);
        String systemType = System.getProperty("os.name").toLowerCase();
//        System.out.println("systemType = " + systemType);
//        System.out.println("System.getProperty(\"user.home\") = " + System.getProperty("user.home"));
        String downloadFolder = "";

        if(systemType.contains("linux")&&userName.equals("jenkins")){
            downloadFolder = "/var/lib/jenkins/workspace/Regression/src/test/resources/Downloads";
        }else if(systemType.contains("windows")){
            downloadFolder = System.getProperty("user.home")+"/Downloads";
        }else if(systemType.contains("mac")){
            downloadFolder = System.getProperty("user.home") +"/Downloads";
        }else if(systemType.contains("linux")&&!userName.equals("jenkins")){
            downloadFolder = System.getProperty("user.home") +"/Downloads";
        }

        return downloadFolder;
    }

    public static String getDownloadPath(String fileName) {

        String userName = System.getProperty("user.name");
//        System.out.println("userName = " + userName);
        String systemType = System.getProperty("os.name").toLowerCase();
//        System.out.println("systemType = " + systemType);
//        System.out.println("System.getProperty(\"user.home\") = " + System.getProperty("user.home"));
        String downloadFolder = "";

        if(systemType.contains("linux")&&userName.equals("jenkins")){
            downloadFolder = "/var/lib/jenkins/workspace/Regression/src/test/resources/Downloads/"+fileName;
        }else if(systemType.contains("windows")){
            downloadFolder = System.getProperty("user.home")+"\\Downloads\\"+fileName;
        }else if(systemType.contains("mac")){
            downloadFolder = System.getProperty("user.home") +"/Downloads/"+fileName;
        }else if(systemType.contains("linux")&&!userName.equals("jenkins")){
            downloadFolder = System.getProperty("user.home") +"/Downloads/"+fileName;
        }

        return downloadFolder;
    }







}
