package javadoc;

public class Main {
    public static void main(String[] args) {
        Core core = new Core();
        core.newBikeOwner("TEST-USER", "TEST-SSN", "TEST-ADDRESS", "TEST-PHONE", "TEST-EMAIL", "Add::ME::SOME::BIKES");
        for (String user : core.list_users()) {
            String[] tokens = user.split(GUIAPI.delimiter);
            System.out.println("string: "+user);
            System.out.println("name: "+tokens[0]);
            System.out.println("ssn: "+tokens[1]);
        }
        BikeOwner owner = new BikeOwner("TEST-USER", "TEST-SSN", "TEST-ADDRESS", "TEST-PHONE", "TEST-EMAIL");
        boolean removed = core.removeBarcode(owner, new Barcode("ME"));
        System.out.println("removed: "+removed);
        for (String user : core.list_users()) {
            String[] tokens = user.split(GUIAPI.delimiter);
            System.out.println("string: "+user);
            System.out.println("name: "+tokens[0]);
            System.out.println("ssn: "+tokens[1]);
        }
        core.removeBikeOwner(owner);
        for (String user : core.list_users()) {
            String[] tokens = user.split(GUIAPI.delimiter);
            System.out.println("string: "+user);
            System.out.println("name: "+tokens[0]);
            System.out.println("ssn: "+tokens[1]);
        }
    }
}
