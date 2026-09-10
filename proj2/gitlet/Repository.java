package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  All specific commands are implemented here
 *
 *  @author jeremy0519
 */
public class Repository {
    // The current working directory.
    public static final File CWD = new File(System.getProperty("user.dir"));
    // The .gitlet directory.
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // The blobs directory.
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    // The commits directory.
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    // The refs file.
    public static final File REFS_FILE = join(GITLET_DIR, "refs");
    // The HEAD file.
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    // The staging file.
    public static final File STAGING_FILE = join(GITLET_DIR, "staging");

    /**
     * Necessary runtime variables,
     * will be read from disk during setup().
     */
    static Staging staging;
    static HashMap<String, String> refs;
    static Commit currentCommit;
    static String currentCommitID;
    static String currentBranch;


    /**
     * The init command creates and initializes the .gitlet repository
     * creates a master branch, and an initial commit.
     */
    public static void init() {
        // abort if .gitlet exists
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        // create .gitlet directory
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        writeContents(HEAD_FILE, "master"); // HEAD file
        staging = new Staging();
        writeObject(STAGING_FILE, staging); // staging file

        // create and save initial commit
        Commit initalCommit = new Commit("initial commit", new Date(0), null);
        String initialCommitID = initalCommit.saveToDisk();
        refs = new HashMap<>();
        refs.put("master", initialCommitID);
        writeObject(REFS_FILE, refs);
    }

    /**
     * For each operation that requires a gitlet repository,
     * the setup command is run before anything else.
     * It sets necessary runtime variables by reading from disk.
     */
    public static void setup() {
        // Abort if not in .gitlet directory
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        staging = readObject(STAGING_FILE, Staging.class);
        refs = readObject(REFS_FILE, HashMap.class);
        currentBranch = readContentsAsString(HEAD_FILE);
        currentCommitID = refs.get(currentBranch);
        currentCommit = readObject(join(COMMITS_DIR, currentCommitID), Commit.class);
    }

    /**
     * The add command adds a file from working directory to the staging area,
     * or marks a file for removal in the staging area.
     */
    public static void add(String filename) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        // SHA-1 of the latest version from working directory
        String newHash = sha1(readContents(file));
        // SHA-1 of the version in current commit, null if not in current commit
        String committedHash = currentCommit.files.get(filename);

        staging.removeIfDeleting(filename);
        if (newHash.equals(committedHash)) {
            // This file is same as in current commit
            staging.removeIfAdding(filename);
        } else {
            // This file is not in current commit
            staging.stageAdd(filename, readContents(file));
        }
    }

    /**
     * The rm command unstages the file if it is staged for addition,
     * and stage it for removal if it is tracked in the current commit
     */
    public static void rm(String filename) {
        boolean staged = staging.adding.containsKey(filename);
        boolean tracked = currentCommit.files.containsKey(filename);

        if (!staged && !tracked) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        staging.removeIfAdding(filename);
        if (tracked) {
            staging.stageDelete(filename);
            restrictedDelete(join(CWD, filename));
        }
    }

    /**
     * The commit command creates a commit based on parent commit,
     * with updates from the staging area
     */
    public static void commit(String msg) {
        if (staging.adding.isEmpty() && staging.deleting.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = new Commit(msg);
        String commitID = commit.saveToDisk();
        refs.put(currentBranch, commitID);
        writeObject(REFS_FILE, refs);

        // Clear staging area
        staging = new Staging();
        writeObject(STAGING_FILE, staging);
    }

    /**
     * Starting at the current head commit,
     * displays information about each commit backwards
     */
    public static void log() {
        logHelper(currentCommitID);
    }

    // Date formatter
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");

    // Recursive helper methods, returns parent commit ID
    private static String printCommit(String commitID) {
        Commit commit = readObject(join(COMMITS_DIR, commitID), Commit.class);
        System.out.println("===");
        System.out.printf("commit %s%n", commitID);
        if (commit.secondParent != null) {
            System.out.printf("Merge %s %s%n", commit.parent, commit.secondParent);
        }
        System.out.printf("Date: %s%n", FORMATTER.format(commit.date));
        System.out.println(commit.message);
        System.out.println();
        return commit.parent;
    }

    private static void logHelper(String commitID) {
        if (commitID == null) {
            return;
        }
        String nextCommitID = printCommit(commitID);
        logHelper(nextCommitID);
    }

    /**
     * The global-log displays information about all commits ever made
     */
    public static void globalLog() {
        for (String commitID : plainFilenamesIn(COMMITS_DIR)) {
            printCommit(commitID);
        }
    }

    /**
     * Prints out the ids of all commits that has the given commit message
     */
    public static void find(String msg) {
        boolean found = false;
        for (String commitID : plainFilenamesIn(COMMITS_DIR)) {
            Commit commit = readObject(join(COMMITS_DIR, commitID), Commit.class);
            if (commit.message.equals(msg)) {
                found = true;
                System.out.println(commitID);
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Copy the file in the commit back into working directory
     */
    public static void checkoutFile(String filename) {
        checkoutFile(currentCommitID, filename);
    }

    public static void checkoutFile(String commitID, String filename) {
        // If using shortened commitID, find out the full commitID first
        commitID = fullCommitID(commitID);
        Commit commit = readObject(join(COMMITS_DIR,commitID), Commit.class);
        String hash = commit.files.get(filename);
        if (hash == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        writeContents(join(CWD, filename), readContents(join(BLOBS_DIR, hash)));
    }

    /**
     * Copy all files from a commit back to working directory,
     * and remove all files tracked in current but not present in that commit
     */
    public static void checkoutCommit(String commitID){
        // If using shortened commitID, find out the full commitID first
        commitID = fullCommitID(commitID);
        Commit commit = readObject(join(COMMITS_DIR,commitID), Commit.class);
        // Check if any file would be overwritten
        commit.files.forEach((filename, hash) -> {
            if (join(CWD, filename).exists() && !currentCommit.files.containsKey(filename)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        });
        // Add (Overwrite) files from new commit
        commit.files.forEach((filename, hash) ->
                writeContents(join(CWD, filename), readContents(join(BLOBS_DIR, hash))));
        // Delete files tracked in current but not present in checked-out commit
        currentCommit.files.keySet().forEach((filename) -> {
            if (!commit.files.containsKey(filename)) {
                restrictedDelete(join(CWD, filename));
            }
        });
        // Clear staging area
        staging = new Staging();
        writeObject(STAGING_FILE, staging);
    }

    /**
     * Helper method to return full commitID if supplied with fewer digits
     * Also handles when no commit exists with that ID
     */
    private static String fullCommitID(String commitID) {
        if (commitID.length()< UID_LENGTH){
            for (String hash: plainFilenamesIn(COMMITS_DIR)){
                if (hash.startsWith(commitID)){
                    commitID = hash;
                }
            }
        }
        File file = join(COMMITS_DIR, commitID);
        if (!file.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return commitID;
    }

    /**
     * Same as checkout commit, except using branch name instead of commitID
     */
    public static void checkoutBranch(String name) {
        String commitID = refs.get(name);
        if (commitID == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (name.equals(currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        checkoutCommit(commitID);
        // Change current branch
        writeContents(HEAD_FILE,name);
    }

    /**
     * Displays all branches that exists, marking current branch with *,
     * and displays what files have been staged for addition or removal
     */
    public static void status(){
        System.out.println("=== Branches ===");
        String[] branches = refs.keySet().toArray(new String[0]); // convert set to array
        Arrays.sort(branches);
        for (String branch:branches){
            if (branch.equals(currentBranch)){
                System.out.println("*"+branch);
            }else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        String[] adding = staging.adding.keySet().toArray(new String[0]);
        Arrays.sort(adding);
        for (String filename: adding){
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        String[] deleting = staging.deleting.toArray(new String[0]);
        Arrays.sort(deleting);
        for (String filename: deleting){
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeMap<String,String> output = new TreeMap<>();
        // Files tracked in current commit:
        currentCommit.files.forEach((filename,hash)->{
            File file = join(CWD,filename);
            if (!file.exists()){
                // deleted from working directory but not staged for removal
                if (!staging.deleting.contains(filename)){
                    output.put(filename,"(deleted)");
                }
            }else{
                // changed in working directory but not staged for addition
                if (!sha1(readContents(file)).equals(hash) && !staging.adding.containsKey(filename)){
                    output.put(filename,"(modified)");
                }
            }
        });
        // Files staged for addition:
        staging.adding.forEach((filename,hash)->{
            File file = join(CWD,filename);
            if (!file.exists()){
                // deleted from working directory
                output.put(filename,"(deleted)");
            }else{
                // changed in the working directory
                if (!sha1(readContents(file)).equals(hash)){
                    output.put(filename,"(modified)");
                }
            }
        });
        output.forEach((filename,status)->{
            System.out.println(filename+" "+status);
        });
        System.out.println();

        System.out.println("=== Untracked files ===");
        for (String filename:plainFilenamesIn(CWD) ){
            // neither staged for addition nor tracked
            if (!staging.adding.containsKey(filename) && !currentCommit.files.containsKey(filename)){
                System.out.println(filename);
            }
            // staged for removal but recreated
            if (staging.deleting.contains(filename)){
                System.out.println(filename);
            }
        }
        System.out.println();
    }

    /**
     * Creates a branch
     */
    public static void branch(String name){
        if (refs.containsKey(name)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        refs.put(name,currentCommitID);
        writeObject(REFS_FILE, refs);
    }

    /**
     * Removes a branch
     */
    public static void removeBranch(String name){
        if (!refs.containsKey(name)){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (currentBranch.equals(name)){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        refs.remove(name);
        writeObject(REFS_FILE, refs);
    }

    /**
     * Resets current branch to a previous commit
     */
    public static void reset(String commitID){
        checkoutCommit(commitID);
        refs.put(currentBranch,commitID);
        writeObject(REFS_FILE, refs);
    }

    /**
     * Merges files from the given branch into the current branch
     */
    public static void merge(String name){
        if (!staging.adding.isEmpty() || !staging.deleting.isEmpty()){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!refs.containsKey(name)){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (currentBranch.equals(name)){
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }
}
