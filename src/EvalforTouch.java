public class EvalforTouch {
    public EvalforTouch(){

    }
    static {
        try{
            Process exec = Runtime.getRuntime().exec("touch /tmp/for3191_2");
            exec.waitFor();
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }
}
