package javadoc;

public class Main {
    public static void main(String[] args) {
        Core core = new Core();
        core.newBikeOwner("TEST-USER", "TEST-SSN", "TEST-ADDRESS", "TEST-PHONE", "TEST-EMAIL", "Add::ME::SOME::BIKES");
        for (BikeOwner owner : core.listUsers()) {
            System.out.println("ssn: "+owner.ssn());
        }
    }
}
