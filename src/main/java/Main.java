import difflib.DiffUtils;
import difflib.Patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static String file = "/*\n" +
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
            "\n" +
            "}";

    public static void main(String[] args) {
        String diff = "--- a/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n+++ b/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n@@ -1,3 +1,22 @@\n+/*\n+ * SonarQube :: GitLab Plugin\n+ * Copyright (C) 2016-2017 Talanlabs\n+ * gabriel.allaigre@talanlabs.com\n+ *\n+ * This program is free software; you can redistribute it and/or\n+ * modify it under the terms of the GNU Lesser General Public\n+ * License as published by the Free Software Foundation; either\n+ * version 3 of the License, or (at your option) any later version.\n+ *\n+ * This program is distributed in the hope that it will be useful,\n+ * but WITHOUT ANY WARRANTY; without even the implied warranty of\n+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n+ * Lesser General Public License for more details.\n+ *\n+ * You should have received a copy of the GNU Lesser General Public License\n+ * along with this program; if not, write to the Free Software Foundation,\n+ * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.\n+ */\n package com.talanlabs.sonar.plugins.gitlab;\n \n import java.util.List;\n";
        Patch<String> p = DiffUtils.parseUnifiedDiff(Arrays.asList(diff.split("\n")));

        List<String> lines = Arrays.asList(file.split("\n"));

        List<String> olds = DiffUtils.unpatch(lines, p);
        System.out.println(olds.stream().collect(Collectors.joining("\n")));

        System.out.println("\n---------------------------------------------------");

        List<String> nums = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            nums.add(String.valueOf(i + 1));
        }
        List<String> olds2 = DiffUtils.unpatch(nums, p);
        System.out.println(olds2.stream().collect(Collectors.joining("\n")));

    }

}
