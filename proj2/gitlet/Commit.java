package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author jeremy0519
 */
public class Commit implements Serializable {
    // The message of this Commit.
    final String message;
    // The time of this commit.
    final Date date;
    // Parent commit id.
    final String parent;
    // Second parent commit id, used in merge commits only.
    final String secondParent;
    // Maps filename to blob location.
    HashMap<String, String> files;

    // Main Constructor
    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.parent = Repository.currentCommitID;
        this.secondParent = null;
        this.files = Repository.currentCommit.files; // initially same as parent commit
        this.files.putAll(Repository.staging.adding); // add or overwrite files staged for addition
        this.files.keySet().removeAll(
                Repository.staging.deleting); // remove files staged for deleting
    }

    /**
     * Constructor with all fields,
     * should be used only when creating the initial commit
     */
    public Commit(String message, Date date, String parent) {
        this.message = message;
        this.date = date;
        this.parent = parent;
        this.secondParent = null;
        this.files = new HashMap<>();
    }

    // Save this commit to disk for persistence
    public String saveToDisk() {
        String sha1 = sha1(serialize(this));
        writeObject(join(Repository.COMMITS_DIR, sha1), this);
        return sha1;
    }
}
