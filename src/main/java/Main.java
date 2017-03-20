import difflib.ChangeDelta;
import difflib.Chunk;
import difflib.Delta;
import difflib.Patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final Pattern PATCH_PATTERN = Pattern.compile("^@@\\s+-(?:(\\d+)(?:,(\\d+))?)\\s+\\+(?:(\\d+)(?:,(\\d+))?)\\s+@@.*$");

    static String FILE_CONTENT = "/*\n" +
            " * SonarQube :: GitLab Plugin\n" +
            " * Copyright (C) 2016-2017 Talanlabs\n" +
            " * gabriel.allaigre@talanlabs.com\n" +
            " *\n" +
            " * This program is free software; you can redistribute it and/or\n" +
            " * modify it under the terms of the GNU Lesser General Public\n" +
            " * License as published by the Free Software Foundation; either\n" +
            " * version 3 of the License, or (at your option) any later version.\n" +
            " *\n" +
            " * This program is distributed in the hope that it will be useful,\n" +
            " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
            " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n" +
            " * Lesser General Public License for more details.\n" +
            " *\n" +
            " * You should have received a copy of the GNU Lesser General Public License\n" +
            " * along with this program; if not, write to the Free Software Foundation,\n" +
            " * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.\n" +
            " */\n" +
            "package com.talanlabs.sonar.plugins.gitlab;\n" +
            "\n" +
            "import java.util.List;\n" +
            "\n" +
            "public class Fake {\n" +
            "\n" +
            "    List<String> ss;\n" +
            "\n" +
            "    public Fake(List<String> ss) {\n" +
            "        this.ss = ss;\n" +
            "    }\n" +
            "\n" +
            "    public void fonction() {\n" +
            "        String toto = null;\n" +
            "        System.out.println(toto.length());\n" +
            "    }\n" +
            "\n" +
            "    public void fonction2() {\n" +
            "        String toto = null;\n" +
            "        System.out.println(toto.length());\n" +
            "    }\n" +
            "}";

    static String DIFF1 = "--- a/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n+++ b/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n@@ -34,5 +34,8 @@ public class Fake {\n         System.out.println(toto.length());\n     }\n \n-\n+    public void fonction2() {\n+        String toto = null;\n+        System.out.println(toto.length());\n+    }\n }\n";
    static String DIFF2 = "--- a/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n+++ b/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n@@ -1,3 +1,22 @@\n+/*\n+ * SonarQube :: GitLab Plugin\n+ * Copyright (C) 2016-2017 Talanlabs\n+ * gabriel.allaigre@talanlabs.com\n+ *\n+ * This program is free software; you can redistribute it and/or\n+ * modify it under the terms of the GNU Lesser General Public\n+ * License as published by the Free Software Foundation; either\n+ * version 3 of the License, or (at your option) any later version.\n+ *\n+ * This program is distributed in the hope that it will be useful,\n+ * but WITHOUT ANY WARRANTY; without even the implied warranty of\n+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n+ * Lesser General Public License for more details.\n+ *\n+ * You should have received a copy of the GNU Lesser General Public License\n+ * along with this program; if not, write to the Free Software Foundation,\n+ * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.\n+ */\n package com.talanlabs.sonar.plugins.gitlab;\n \n import java.util.List;\n";

    public static void main(String[] args) {
        String fileContent = FILE_CONTENT;
        for (String diff : Arrays.asList(DIFF1, DIFF2)) {
            fileContent = patch(fileContent, diff);
            System.out.println(fileContent);
        }

        Patch<String> p = parseUnifiedDiff(Arrays.asList(DIFF1.split("\n")));

        List<String> lines = Arrays.asList(FILE_CONTENT.split("\n"));

        List<String> nums = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            nums.add("#" + String.valueOf(i + 1) + "#");
        }

        List<String> nums2 = new ArrayList<>(nums);
        for (Delta<String> d : p.getDeltas()) {
            switch (d.getType()) {
                case INSERT:
                    insert(d, nums2);
                    break;
                case CHANGE:
                    change(d, nums2);
                    break;
                case DELETE:
                    delete(d, nums2);
                    break;
            }
        }

        //List<String> olds2 = DiffUtils.unpatch(nums, p);
        System.out.println(nums2.stream().collect(Collectors.joining("\n")));
        System.out.println("\n---------------------------------------------------");
    }

    private static String patch(String fileContent, String diff) {
        Patch<String> p = parseUnifiedDiff(Arrays.asList(diff.split("\n")));

        List<String> lines = Arrays.asList(fileContent.split("\n"));

        List<String> nums = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            nums.add("#" + (i + 1) + "#");
        }

        List<String> nums2 = new ArrayList<>(nums);
        for (Delta<String> d : p.getDeltas()) {
            switch (d.getType()) {
                case INSERT:
                    insert(d, nums2);
                    break;
                case CHANGE:
                    change(d, nums2);
                    break;
                case DELETE:
                    delete(d, nums2);
                    break;
            }
        }

        return nums2.stream().collect(Collectors.joining("\n"));
    }

    private static void insert(Delta<String> delta, List<String> target) {
        int position = delta.getRevised().getPosition();
        List<String> rlines = delta.getRevised().getLines();
        for (String rline : rlines) {
            if (rline != null) {
                target.remove(position);
            } else {
                position++;
            }
        }
    }

    private static void change(Delta<String> delta, List<String> target) {
        int position = delta.getRevised().getPosition();
        List<String> rlines = delta.getRevised().getLines();
        for (String rline : rlines) {
            if (rline != null) {
                target.remove(position);
            } else {
                position++;
            }
        }
        List<String> olines = delta.getOriginal().getLines();
        for (int i = 0; i < olines.size(); i++) {
            if (olines.get(i) != null) {
                target.add(position + i, "");
            }
        }
    }

    private static void delete(Delta<String> delta, List<String> target) {
        int position = delta.getRevised().getPosition();
        List<String> olines = delta.getOriginal().getLines();
        for (int i = 0; i < olines.size(); i++) {
            if (olines.get(i) != null) {
                target.add(position + i, "");
            }
        }
    }

    public static Patch<String> parseUnifiedDiff(List<String> diff) {
        boolean inPrelude = true;
        List<String[]> rawChunk = new ArrayList<String[]>();
        Patch<String> patch = new Patch<String>();

        int old_ln = 0;
        int new_ln = 0;
        for (String line : diff) {
            // Skip leading lines until after we've seen one starting with '+++'
            if (inPrelude) {
                if (line.startsWith("+++")) {
                    inPrelude = false;
                }
                continue;
            }
            Matcher m = PATCH_PATTERN.matcher(line);
            if (m.find()) {
                // Process the lines in the previous chunk
                addPatch(patch, rawChunk, old_ln, new_ln);

                // Parse the @@ header
                old_ln = m.group(1) == null ? 1 : Integer.parseInt(m.group(1));
                new_ln = m.group(3) == null ? 1 : Integer.parseInt(m.group(3));

                if (old_ln == 0) {
                    old_ln += 1;
                }
                if (new_ln == 0) {
                    new_ln += 1;
                }
            } else {
                if (line.length() > 0) {
                    String tag = line.substring(0, 1);
                    String rest = line.substring(1);
                    if (tag.equals(" ") || tag.equals("+") || tag.equals("-")) {
                        rawChunk.add(new String[]{tag, rest});
                    }
                } else {
                    rawChunk.add(new String[]{" ", ""});
                }
            }
        }

        // Process the lines in the last chunk
        addPatch(patch, rawChunk, old_ln, new_ln);

        return patch;
    }

    private static void addPatch(Patch<String> patch, List<String[]> rawChunk, int oldPosition, int newPosition) {
        if (rawChunk.isEmpty()) {
            List<String> oldChunkLines = new ArrayList<>();
            List<String> newChunkLines = new ArrayList<>();

            for (String[] raw_line : rawChunk) {
                String tag = raw_line[0];
                String rest = raw_line[1];
                if (tag.equals("-")) {
                    oldChunkLines.add(rest);
                }
                if (tag.equals("+")) {
                    newChunkLines.add(rest);
                }
                if (tag.equals(" ")) {
                    oldChunkLines.add(null);
                    newChunkLines.add(null);
                }
            }
            patch.addDelta(new ChangeDelta<>(new Chunk<>(
                    oldPosition - 1, oldChunkLines), new Chunk<>(
                    newPosition - 1, newChunkLines)));
            rawChunk.clear();
        }
    }
}
