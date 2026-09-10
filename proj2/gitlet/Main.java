package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author jeremy0519
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // Handle no command
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.init();
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length == 1 || (args.length == 2 && args[1].isEmpty())) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                } else if (args.length > 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.commit(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.rm(args[1]);
                break;
            case "log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.log();
                break;
            case "global-log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.globalLog();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.find(args[1]);
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    Repository.setup();
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    Repository.setup();
                    Repository.checkoutFile(args[1], args[3]);
                } else if (args.length == 2) {
                    Repository.setup();
                    Repository.checkoutBranch(args[1]);
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "status":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.status();
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.reset(args[1]);
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.setup();
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
