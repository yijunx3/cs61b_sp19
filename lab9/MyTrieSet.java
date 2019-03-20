import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class MyTrieSet implements TrieSet61B {

    private TrieNode root;

    public MyTrieSet() {
        root = new TrieNode('\0', false);
    }

    /** Clears all items out of Trie */
    @Override
    public void clear() {
        root = null;
    }

    /** Returns true if the Trie contains KEY, false otherwise */
    @Override
    public boolean contains(String key) {
        if (key == null || key.length() == 0 || root == null) {
            return false;
        }
        TrieNode currNode = root;
        TrieNode nextNode = null;
        for (int i = 0; i < key.length(); i += 1) {
            char c = key.charAt(i);
            nextNode = currNode.children.get(c);
            if (nextNode == null) {
                return false;
            }
            currNode = nextNode;
        }
        return currNode.isLeaf;
    }

    /** Inserts string KEY into Trie */
    @Override
    public void add(String key) {
        if (key == null || key.length() == 0 || root == null) {
            return;
        }
        TrieNode currNode = root;
        for (int i = 0; i < key.length(); i += 1) {
            char c = key.charAt(i);
            if (!currNode.children.containsKey(c)) {
                currNode.children.put(c, new TrieNode(c, false));
            }
            currNode = currNode.children.get(c);
        }
        currNode.isLeaf = true;
    }

    /** Returns a list of all words that start with PREFIX */
    @Override
    public List<String> keysWithPrefix(String prefix) {
        if (prefix == null || prefix.length() == 0 || root == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        TrieNode startNode = root;
        for (int i = 0; i < prefix.length(); i += 1) {
            char c = prefix.charAt(i);
            startNode = startNode.children.get(c);
        }
        for (TrieNode currNode : startNode.children.values()) {
            if (currNode != null) {
                keysWithPrefix(result, prefix, currNode);
            }
        }
        return result;
    }

    private void keysWithPrefix(List<String> result, String word, TrieNode currNode) {
        if (currNode.isLeaf) {
            result.add(word + currNode.nodeChar);
        }
        for (TrieNode nextNode : currNode.children.values()) {
            if (nextNode != null) {
                keysWithPrefix(result, word + currNode.nodeChar, nextNode);
            }
        }
    }

    /** Returns the longest prefix of KEY that exists in the Trie
     * Not required for Lab 9. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public String longestPrefixOf(String key) {
        if (!contains(key)) {
            return null;
        }
        String longestPrefix = "";
        String tempPrefix = "";
        TrieNode currNode = root;
        for (int i = 0; i < key.length(); i += 1) {
            char c = key.charAt(i);
            if (currNode.children.keySet().size() > 1) {
                longestPrefix = tempPrefix;
            }
            tempPrefix += c;
            currNode = currNode.children.get(c);
        }
        return longestPrefix;
    }

    private class TrieNode {
        private char nodeChar;
        private boolean isLeaf; // If it is a leaf node, then the nodeChar is a key.
        private Map<Character, TrieNode> children;

        public TrieNode(char nodeChar, boolean isLeaf) {
            this.nodeChar = nodeChar;
            children = new HashMap();
            this.isLeaf = isLeaf;
        }

    }

    /*public static void main(String[] args) {
        MyTrieSet trie = new MyTrieSet();
        trie.add("hi");
        trie.add("hello");
        trie.add("help");
        trie.add("zebra");
        trie.add("homonym");
        trie.add("homophone");
        trie.add("homosexual");

        System.out.println(trie.contains("hello"));
        System.out.println(trie.keysWithPrefix("h"));
        System.out.println(trie.longestPrefixOf("hello"));
        System.out.println(trie.keysWithPrefix("homo"));
        System.out.println(trie.longestPrefixOf("homophone"));
    }*/
}