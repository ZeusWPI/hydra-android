/*
 * Copyright (C) 2016 Niko Strijbol
 * Copyright (C) 2013-2014 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright (C) 2013 Mohammed Lakkadshaw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.common.ui.html;

import android.text.*;
import android.text.style.*;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.XMLReader;

/**
 * Custom HTML tag handler to support more tags.
 * <p>
 * Currently the tag handler supports:
 * <ul>
 *  <li>ul</li>
 *  <li>ol</li>
 *  <li>dd</li>
 *  <li>li</li>
 *  <li>code</li>
 *  <li>center</li>
 * </ul>
 * <p>
 * More elements can be added. However, some elements, such as center, are no longer part of the HTML standard. Thus,
 * this is 'pseudo-HTML'.
 * <p>
 * This class is based on work in <a href="https://github.com/skimarxall/RealTextView">RealTextView</a>.
 *
 * @author Niko Strijbol
 * @author Dominik Schürmann
 * @author Mohammed Lakkadshaw
 */
public class HtmlTagHandler implements Html.TagHandler {

    private static final String TAG = "TagHandler";
    private final List<String> listParents = new ArrayList<>();
    private int listItemCount;

    /**
     * Mark the opening tag by using private classes
     */
    private static void start(Editable output, Object mark) {
        int len = output.length();
        output.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);

        Log.v(TAG, "len: " + len);
    }

    private static <T> void end(Editable output, Class<T> kind, Object repl, boolean paragraphStyle) {
        T obj = getLast(output, kind);
        // start of the tag
        int where = output.getSpanStart(obj);
        // end of the tag
        int len = output.length();

        output.removeSpan(obj);

        if (where != len) {
            // paragraph styles like AlignmentSpan need to end with a new line!
            if (paragraphStyle) {
                output.append("\n");
                len++;
            }
            output.setSpan(repl, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        Log.v(TAG, "where: " + where);
        Log.v(TAG, "len: " + len);
    }

    /**
     * Get last marked position of a specific tag kind.
     *
     * @param text The text.
     * @param kind The kind of tag.
     * @return The last marked position.
     */
    @Nullable
    private static <T> T getLast(Editable text, Class<T> kind) {
        T[] spans = text.getSpans(0, text.length(), kind);

        //Begin at the back of the list.
        for (int i = spans.length; i > 0; i--) {
            if (text.getSpanFlags(spans[i - 1]) == Spannable.SPAN_MARK_MARK) {
                return spans[i - 1];
            }
        }
        return null;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        boolean isList = tag.equalsIgnoreCase("ul") || tag.equalsIgnoreCase("ol") || tag.equalsIgnoreCase("dd");
        if (opening) {
            // opening tag
            Log.v(TAG, "opening, output: " + output.toString());

            if (isList) {
                listParents.add(tag);
                listItemCount = 0;
            } else if (tag.equalsIgnoreCase("code")) {
                start(output, new Code());
            } else if (tag.equalsIgnoreCase("center")) {
                start(output, new Center());
            }
        } else {
            // closing tag
            Log.v(TAG, "closing, output: " + output.toString());

            if (isList) {
                listParents.remove(tag);
                listItemCount = 0;
            } else if (tag.equalsIgnoreCase("li")) {
                handleListTag(output);
            } else if (tag.equalsIgnoreCase("code")) {
                end(output, Code.class, new TypefaceSpan("monospace"), false);
            } else if (tag.equalsIgnoreCase("center")) {
                end(output, Center.class, new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), true);
            }
        }
    }

    private void handleListTag(Editable output) {
        if (listParents.get(listParents.size() - 1).equals("ul")) {
            output.append("\n");
            String[] split = output.toString().split("\n");

            int lastIndex = split.length - 1;
            int start = output.length() - split[lastIndex].length() - 1;
            output.setSpan(new BulletSpan(15 * listParents.size()), start, output.length(), 0);
        } else if (listParents.get(listParents.size() - 1).equals("ol")) {
            listItemCount++;

            output.append("\n");
            String[] split = output.toString().split("\n");

            int lastIndex = split.length - 1;
            int start = output.length() - split[lastIndex].length() - 1;
            output.insert(start, listItemCount + ". ");
            output.setSpan(new LeadingMarginSpan.Standard(15 * listParents.size()), start, output.length(), 0);
        }
    }

    private static class Code {
    }

    private static class Center {
    }
}
