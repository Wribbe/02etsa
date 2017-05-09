package javadoc;

/**
 * @author AUTHOR-TAG
 * @version 0.9
 * @since   0.1
 */

/**
 * Hardware interface for ElectronicLock.
 * */
public interface ElectronicLock {

    /**
     * Opens the lock for duration sections.
     * @param duration the number of seconds the lock should stay open.
     * */
    public void open(int duration);
}

