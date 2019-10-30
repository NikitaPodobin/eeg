import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException, GitAPIException {
        Repository repository = Git.open(new File(".git"))
                .checkout().getRepository();
       /* Ref head = repository.findRef("HEAD");

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = commit.getTree();
            System.out.println("Having tree: " + tree);

            // now use a TreeWalk to iterate over all files in the Tree recursively
            // you can set Filters to narrow down the results if needed
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                while (treeWalk.next()) {
                    System.out.println("found: " + treeWalk.getPathString());
                }
            }
        }*/
        Map<String, Integer> filesCommit = new HashMap<>();
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().all().call();
            for (RevCommit commit : commits) {
                System.out.println("LogCommit: " + commit);
                RevTree tree = commit.getTree();
                System.out.println("Having tree: " + tree);
                try (RevWalk revWalk = new RevWalk(repository)) {
                    try (TreeWalk treeWalk = new TreeWalk(repository)) {
                        treeWalk.addTree(tree);
                        treeWalk.setRecursive(true);
                        while (treeWalk.next()) {
                            Integer count = 1;
                            if (filesCommit.containsKey(treeWalk.getPathString())) {
                                count += filesCommit.get(treeWalk.getPathString());
                            }
                            filesCommit.put(treeWalk.getPathString(), count);
                            System.out.println("found: " + treeWalk.getPathString());
                        }
                    }
                    revWalk.dispose();
                }
            }
        }
        for (Map.Entry<String, Integer> entry : filesCommit.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println("File name: " + key + " File count:" + value);
        }
    }
}

