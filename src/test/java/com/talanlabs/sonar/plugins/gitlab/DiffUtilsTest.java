/*
 * SonarQube :: GitLab Plugin
 * Copyright (C) 2016-2017 Talanlabs
 * gabriel.allaigre@talanlabs.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.talanlabs.sonar.plugins.gitlab;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DiffUtilsTest {

    private static String FILE_CONTENT = "/*\n" +
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
            "        String buildOldLines = null;\n" +
            "        System.out.println(buildOldLines.length());\n" +
            "    }\n" +
            "\n" +
            "    public void fonction2() {\n" +
            "        String buildOldLines = null;\n" +
            "        System.out.println(buildOldLines.length());\n" +
            "    }\n" +
            "}";

    @Test
    public void testChange() {
        String diff1 = "--- a/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n+++ b/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n@@ -34,5 +34,8 @@ public class Fake {\n         System.out.println(buildOldLines.length());\n     }\n \n-\n+    public void fonction2() {\n+        String buildOldLines = null;\n+        System.out.println(buildOldLines.length());\n+    }\n }\n";
        String diff2 = "--- a/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n+++ b/src/main/java/com/talanlabs/sonar/plugins/gitlab/Fake.java\n@@ -1,3 +1,22 @@\n+/*\n+ * SonarQube :: GitLab Plugin\n+ * Copyright (C) 2016-2017 Talanlabs\n+ * gabriel.allaigre@talanlabs.com\n+ *\n+ * This program is free software; you can redistribute it and/or\n+ * modify it under the terms of the GNU Lesser General Public\n+ * License as published by the Free Software Foundation; either\n+ * version 3 of the License, or (at your option) any later version.\n+ *\n+ * This program is distributed in the hope that it will be useful,\n+ * but WITHOUT ANY WARRANTY; without even the implied warranty of\n+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n+ * Lesser General Public License for more details.\n+ *\n+ * You should have received a copy of the GNU Lesser General Public License\n+ * along with this program; if not, write to the Free Software Foundation,\n+ * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.\n+ */\n package com.talanlabs.sonar.plugins.gitlab;\n \n import java.util.List;\n";


        List<Integer> lines = new DiffUtils().buildOldLines(Arrays.asList(FILE_CONTENT.split("\n")).size(), Arrays.asList(diff1, diff2));
        Assertions.assertThat(lines.indexOf(34) + 1).isEqualTo(15);
    }

}
