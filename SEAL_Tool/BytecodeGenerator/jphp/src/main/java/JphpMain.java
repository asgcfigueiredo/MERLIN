public class JphpMain {
    public static void main(String args[]) {
        Launcher launcher = new Launcher();
        if(args.length == 0) {
            launcher.run("");
        }
        else {
            for(String file : args) {
                launcher.run(file);
            }
        }
    }
}
