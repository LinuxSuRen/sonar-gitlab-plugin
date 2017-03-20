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
import java.util.stream.Stream;

public class DiffUtils {

    private static final Pattern PATCH_PATTERN = Pattern.compile("^@@\\s+-(?:(\\d+)(?:,(\\d+))?)\\s+\\+(?:(\\d+)(?:,(\\d+))?)\\s+@@.*$");

    public List<Integer> buildOldLines(int nbLines, List<String> diffs) {
        List<Integer> nums = Stream.iterate(1, i -> ++i).limit(nbLines).collect(Collectors.toList());

        if (diffs != null) {
            for (String diff : diffs) {
                nums = patch(nums, diff);
            }
        }

        return nums;
    }

    private List<Integer> patch(List<Integer> nums, String diff) {
        Patch<String> patch = parseUnifiedDiff(Arrays.asList(diff.split("\n")));

        List<Integer> res = new ArrayList<>(nums);
        for (Delta<String> d : patch.getDeltas()) {
            switch (d.getType()) {
                case CHANGE:
                    change(d, res);
                    break;
            }
        }

        return res;
    }

    private void change(Delta<String> delta, List<Integer> target) {
        int position = delta.getRevised().getPosition();
        List<String> rlines = delta.getRevised().getLines();
        for (String rline : rlines) {
            if (rline != null) {
                target.remove(position);
            } else {
                position++;
            }
        }
        position = delta.getRevised().getPosition();
        List<String> olines = delta.getOriginal().getLines();
        for (int i = 0; i < olines.size(); i++) {
            if (olines.get(i) != null) {
                target.add(position + i, null);
            }
        }
    }

    private Patch<String> parseUnifiedDiff(List<String> diff) {
        boolean inPrelude = true;
        List<String[]> rawChunk = new ArrayList<String[]>();
        Patch<String> patch = new Patch<String>();

        int oldPosition = 0;
        int newPosition = 0;
        for (String line : diff) {
            if (inPrelude) {
                if (line.startsWith("+++")) {
                    inPrelude = false;
                }
                continue;
            }
            Matcher m = PATCH_PATTERN.matcher(line);
            if (m.find()) {
                // Process the lines in the previous chunk
                addPatch(patch, rawChunk, oldPosition, newPosition);

                // Parse the @@ header
                oldPosition = m.group(1) == null ? 1 : Integer.parseInt(m.group(1));
                newPosition = m.group(3) == null ? 1 : Integer.parseInt(m.group(3));

                if (oldPosition == 0) {
                    oldPosition += 1;
                }
                if (newPosition == 0) {
                    newPosition += 1;
                }
            } else {
                parseNoMatchLine(line, rawChunk);
            }
        }

        // Process the lines in the last chunk
        addPatch(patch, rawChunk, oldPosition, newPosition);

        return patch;
    }

    private void parseNoMatchLine(String line, List<String[]> rawChunk) {
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

    private void addPatch(Patch<String> patch, List<String[]> rawChunk, int oldPosition, int newPosition) {
        if (!rawChunk.isEmpty()) {
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
