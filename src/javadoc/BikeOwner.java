package javadoc;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents the data composing a BikeOwner inside of the Easy Parks
 * software stack.
 *
 * @author AUTHOR-TAG
 * @version 0.9
 * @since 0.1
 */
public class BikeOwner implements ListElement{

    private int num_args = 5;
    private String[] fields = new String[num_args];

    /**
     * Create a new BikeOwner instance.
     *
     * @param args 5 consecutive strings; name, ssn, address, phone and email.
     */
    public BikeOwner(String... args) {

        int i = 0;
        int argc = args.length;
        List<String> strings = Arrays.asList(args);
        for (; i<argc; i++) {
            this.fields[i] = args[i];
        }
        // Not enough parameters, default to empty string.
        for (; i<num_args; i++) {
            this.fields[i] = "";
        }
    }
    /**
     * @return users name.
     */
    public String name() {return this.fields[0];}
    /**
     * @return users ssn.
     */
    public String ssn() {return this.fields[1];}
    /**
     * @return users address.
     */
    public String address() {return this.fields[2];}
    /**
     * @return users phone.
     */
    public String phone() {return this.fields[3];}
    /**
     * @return users email.
     */
    public String email() {return this.fields[4];}

    /**
     * @return users as String.
     */
    public String toString() {
        return name();
    }
}
