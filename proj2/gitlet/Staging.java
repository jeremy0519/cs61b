package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Repository.BLOBS_DIR;
import static gitlet.Utils.*;

/**
 * Represents the staging area.
 *
 * @author jeremy0519
 */
public class Staging implements Serializable {
    // Maps added (modified) files to blobs (SHA-1 filenames)
    HashMap<String, String> adding = new HashMap<>();
    // Records files to be removed
    HashSet<String> deleting = new HashSet<>();

    // Removes a file from blob and adding if it is staged for adding
    public void removeIfAdding(String filename) {
        String hash = adding.get(filename);
        if (hash != null) {
            // This file is in staging area
            join(BLOBS_DIR, hash).delete();
            adding.remove(filename);
            writeObject(Repository.STAGING_FILE, this);
        }
    }

    // Removes a file from deleting if it is staged for deleting
    public void removeIfDeleting(String filename) {
        deleting.remove(filename);
        writeObject(Repository.STAGING_FILE, this);
    }

    // Adds or updates a file into adding
    public void stageAdd(String filename, byte[] contents) {
        String stagedHash = adding.get(filename);
        String newHash = sha1(contents);
        if (stagedHash != null) {
            // This file is in staging area, so remove old staged blob
            join(BLOBS_DIR, stagedHash).delete();
        }
        writeContents(join(BLOBS_DIR, newHash), contents);
        adding.put(filename, newHash);
        writeObject(Repository.STAGING_FILE, this);
    }

    // Adds file into removing
    public void stageRemove(String filename) {
        deleting.add(filename);
        writeObject(Repository.STAGING_FILE, this);
    }
}
