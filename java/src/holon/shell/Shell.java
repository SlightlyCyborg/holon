package holon.shell;

import java.util.logging.Logger;
import java.io.*;

import java.lang.reflect.Method;
import java.lang.ClassLoader;
import java.net.URLClassLoader;
import java.util.Vector;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;



public class Shell implements Runnable{
    final String classPath = "/home/alex/Projects/holon/build/classes/";
    String usr, host;
    File dir;
    Logger logger = Logger.getLogger("shell");
    BufferedReader in;
    PrintStream out = System.out;
    private Map<String, String> imports;

    private Shell() {
        initPath();
        initSystemProps();
        initConsole();
        initContext();
    };

    private void initContext() {
        imports = new HashMap<>();
    }

    private void initConsole() {
        InputStreamReader reader = new InputStreamReader(System.in);
        in = new BufferedReader(reader);
    }

    private void initPath() {
        String path = System.getenv().get("PATH");
    }

    private void initSystemProps() {
        try{
            usr = System.getProperty("user.name", "noface");
            dir = new File(System.getProperty("user.home", "/"));
            host = System.getProperty("machine.name", "holon");
        } catch (SecurityException e) {
            logger.warning("Shell's security manager prevents it from checking system properties");
        } catch (Exception e) {
            logger.severe("Either system property key was empty or null");
        }
    }

    public static Shell getHomeShell(){
        Shell s = new Shell();
        return s;
    }

    public static void main(String[] args) {
        Shell s = getHomeShell();
        s.run();

    }

    private String getFullClassName(String anyName) {
        String fullName = imports.get(anyName);
        if(fullName == null) {
            return anyName;
        } else {
            return fullName;
        }
    }

    private void testClass(String[] command) {
        try{
            ClassLoader classLoader = new ShellClassLoader(
                    classPath, Shell.class.getClassLoader());
            Class testRunner = classLoader.loadClass("holon.shell.TestRunner");

            Class[] testClasses = new Class[command.length-1];
            for(int i = 1; i < command.length; i++) {
                String fullClassName = getFullClassName(command[i]);
                testClasses[i-1] = classLoader.loadClass(fullClassName);
            }
            if(testClasses.length > 0) {
                try {
                    Method runTests = testRunner.getMethod("runTests", Class[].class);
                    Object result = runTests.invoke(null, new Object[]{testClasses});
                } catch(Exception e){}
            }
        } catch (ClassNotFoundException exception) {}
    }

    private void importClass(String fullClassName) {
        String[] packageComponents = fullClassName.split("\\.");
        if(packageComponents.length > 1) {
            String className = packageComponents[packageComponents.length-1];
            imports.put(className, fullClassName);
        }
    }

    private void shellLoop() {
        String[] command = new String[]{};

        while(command.length < 1 || !command[0].equals("exit")) {
            showPrompt();
            command = getCommand();

            if(command.length > 0) {
                switch(command[0]) {
                    case "test":
                        testClass(command);
                        break;
                    case "import":
                        importClass(command[1]);
                        break;
                    default:
                        runJavaProgram(command);
                }
            }
        }
    }

    public void runJavaProgram(String[] command) {
        String[] args = new String[command.length-1];
        System.arraycopy(command, 1, args, 0, command.length-1);
        String fullClassName = getFullClassName(command[0]);
        ClassLoader classLoader = new ShellClassLoader(
                classPath, Shell.class.getClassLoader());

        try {
            Class c = classLoader.loadClass(fullClassName);
            Method main = c.getMethod("main", String[].class);
            Object result = main.invoke(null, new Object[]{args});
        } catch (Exception e) {
            System.out.printf("Couldn't run java program: %s\n", fullClassName);
        }
    }

    public void run() {
        printWelcome();
        shellLoop();
        printGoodbye();
    }

    private void printWelcome() {
        out.println("holon.shell booting up ...");
    }

    private void printGoodbye() {
        out.println("holon.shell shutting down ...");
    }

    private String[] preProcessCommand(String command) {
        return command.trim().split("\\s");
    }

    private String[] getCommand() {
        String command = "";
        try {
            command = in.readLine();
            return preProcessCommand(command);
        } catch (Exception e) {
            return new String[]{command};
        }
    }

    private void showPrompt() {
        out.printf("[%s@%s %s]$ ", usr, host, dir.getName());
    }
}
