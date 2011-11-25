package com.cyrilpottiers.androlib.strings;

import java.util.ArrayList;

import android.content.Context;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.View;

/**
 * <p>
 * Divers operations for <code>String</code>s.
 * </p>
 * 
 * @author C3PO
 */

public class StringUtils {

    /**
     * <p>
     * Public class for style string
     * </p>
     */
    public static class SpanStyle {
        public CharacterStyle whats;
        public int            start;
        public int            end;
        public int            flags;
    }

    /**
     * <p>
     * Public method which purpose is to format a pattern string and extract
     * span style
     * </p>
     * <p>
     * Pattern are :
     * <li>[[textcolor=0xXXXXXXXX]]text[[\\]]
     * <li>[[backcolor=0xXXXXXXXX]]text[[\\]]
     * <li>[[bold]]text[[\\]]
     * <li>[[italic]]text[[\\]]
     * <li>[[link]]text[[\\]]
     * </p>
     * 
     * <p>
     * Exemple of use :
     * 
     * <pre>
     * {@code
     * ArrayList<SpanStyle> spans = new ArrayList<SpanStyle>();
     * text_view.setText(StringUtils.formatSpan(pattern, spans), TextView.BufferType.SPANNABLE);
     * Spannable sText = (Spannable) text_view.getText();
     * for (SpanStyle style : spans) {
     *   if (style.whats instanceof ClickableSpan) {
     *     style.whats = new ClickableSpan() {
     *      &#64;Override
     *      public void onClick(View widget) {
     *      }
     *     };
     *   }
     *   sText.setSpan(style.whats, style.start, style.end, style.flags);
     * }
     * </pre>
     * 
     * </p>
     * 
     * @param pattern
     *            the pattern string to format
     * @param spans
     *            the array which all the span style will be put
     * @return the input string without patterns
     */
    public static String formatSpan(Context context, String pattern, ArrayList<SpanStyle> spans) {
        if (pattern == null) return null;
        StringBuilder sb = new StringBuilder();
        // lookup [[...]]
        int beginString = 0, beginResult = 0;
        boolean b = true;
//        Log.v(LOG, "-------------------------------------");
//        Log.v(LOG, "formatSpan string=" + pattern);
        while (b) {
            int i = pattern.indexOf("[[", beginString);
            int j = pattern.indexOf("]]", i);
            int k = pattern.indexOf("[[", j);
            int l = pattern.indexOf("]]", k);

//            Log.v(LOG, "begin =" + beginString + " i=" + i + " j=" + j + " k=" + k + " l=" + l);

            if (i >= 0 && j >= 0) {
                sb.append(pattern.substring(beginString, i));
                beginResult = sb.length();
                // detection style
                String inner = pattern.substring(i + 2, j);
//                Log.v(LOG, "    inner=" + inner);
                String[] inners = inner.split(",");
                // eof
                if (k < 0 || l < 0) {
                    sb.append(pattern.substring(j + 2));
                    l = sb.length();
                    b = false;
                }
                else {
                    sb.append(pattern.substring(j + 2, k));
                    l = beginResult + k - (j + 2);
                }
//                Log.v(LOG, "    l'=" + l);
                for (String v : inners) {
                    v = v.trim();
//                    Log.v(LOG, "    keyvalue=" + v);
                    SpanStyle sStyle = new SpanStyle();
                    if (v.startsWith("textcolor")) {
                        j = v.indexOf("=");
                        if (j >= 0 && j < v.length() - 1) {
                            v = v.substring(j + 1).trim();
                            j = 0;
                            try {
                                j = intFromHexString(v);
                            }
                            catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
//                            Log.v(LOG, "    value t=" + v + " n=" + j);
                            sStyle.whats = new ForegroundColorSpan(j);
                        }
                    }
                    else if (v.startsWith("backcolor")) {
                        j = v.indexOf("=");
                        if (j >= 0 && j < v.length() - 1) {
                            v = v.substring(j + 1).trim();
                            j = 0;
                            try {
                                j = intFromHexString(v);
                            }
                            catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
//                            Log.v(LOG, "    value t=" + v + " n=" + j);
                            sStyle.whats = new BackgroundColorSpan(j);
                        }
                    }
                    else if (v.startsWith("bold")) {
                        sStyle.whats = new StyleSpan(android.graphics.Typeface.BOLD);
                    }
                    else if (v.startsWith("italic")) {
                        sStyle.whats = new StyleSpan(android.graphics.Typeface.ITALIC);
                    }
                    else if (v.startsWith("image")) {
                        j = v.indexOf("=");
                        if (j >= 0 && j < v.length() - 1) {
                            v = v.substring(j + 1).trim();
                            j = 0;
                            try {
                                j = Integer.parseInt(v);
                            }
                            catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
//                            Log.v(LOG, "    value t=" + v + " n=" + j);
                            sStyle.whats = new ImageSpan(context,j);
                        }
                    }
                    else if (v.startsWith("link")) {
                        sStyle.whats = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                            }
                        };
                    }
                    else
                        continue;
                    sStyle.start = beginResult;
                    sStyle.end = l;
                    sStyle.flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
                    if (sStyle.end > sStyle.start) spans.add(sStyle);
//                    Log.v(LOG, "    whats=" + sStyle.whats + " start=" + sStyle.start+" end="+sStyle.end);
                }
            }
            else {
                sb.append(pattern.substring(beginString));
                b = false;
            }
            beginString = k;
        }

//        Log.v(LOG, "endstring=" + sb.toString());
        return sb.toString();
    }

    /**
     * <p>
     * Public method which purpose is to format an hexastring 0xXXXXXXXX or
     * #XXXXXXXX to an int
     * </p>
     * 
     * @param hexa
     *            the hexastring to format
     * @return the int value of the hexastring
     */
    public static int intFromHexString(String hexa)
            throws NumberFormatException {
        int radix = 16;
        int index = 0;
        int result = 0;

        // Handle radix specifier, if present
        if (hexa.startsWith("0x", index) || hexa.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        }
        else if (hexa.startsWith("#", index)) {
            index++;
            radix = 16;
        }
        if (hexa.length() - index > 8) {
            throw new NumberFormatException(hexa + " exceeds 8 digits");
        }
        int digit = 0;
        for (int i = index; i < hexa.length(); i++) {
            result = result << 4;
            digit = Character.digit(hexa.charAt(i), radix);
            if (digit == -1) {
                throw new NumberFormatException(hexa + " : \'" + hexa.charAt(i) + "\' is not a valid hex digit");
            }
            result |= digit;
        }
        return result;
    }
    
    public static char getUnaccentedChar(char letter) {
        switch (letter) {
            case '\u00C0':
            case '\u00C1':
            case '\u00C2':
            case '\u00C3':
            case '\u00C4':
            case '\u00C5':
            case '\u00C6':
            case '\u0100':
            case '\u0102':
            case '\u0104':
                letter = 'A';
                break;
            case '\u00E0':
            case '\u00E1':
            case '\u00E2':
            case '\u00E3':
            case '\u00E4':
            case '\u00E5':
            case '\u00E6':
            case '\u0101':
            case '\u0103':
            case '\u0105':
                letter = 'a';
                break;
            case '\u00C7':
            case '\u0106':
            case '\u0108':
            case '\u010A':
            case '\u010C':
                letter = 'C';
                break;
            case '\u00E7':
            case '\u0107':
            case '\u0109':
            case '\u010B':
            case '\u010D':
                letter = 'c';
                break;
            case '\u00D0':
            case '\u010E':
            case '\u0110':
                letter = 'D';
                break;
            case '\u010F':
            case '\u0111':
                letter = 'd';
                break;
            case '\u00C8':
            case '\u00C9':
            case '\u00CA':
            case '\u00CB':
            case '\u0112':
            case '\u0114':
            case '\u0116':
            case '\u0118':
            case '\u011A':
                letter = 'E';
                break;
            case '\u00E8':
            case '\u00E9':
            case '\u00EA':
            case '\u00EB':
            case '\u0113':
            case '\u0115':
            case '\u0117':
            case '\u0119':
            case '\u011B':
                letter = 'e';
                break;
            case '\u011C':
            case '\u011E':
            case '\u0120':
            case '\u0122':
                letter = 'G';
                break;
            case '\u011D':
            case '\u011F':
            case '\u0121':
            case '\u0123':
                letter = 'g';
                break;
            case '\u0124':
            case '\u0126':
                letter = 'H';
                break;
            case '\u0125':
            case '\u0127':
                letter = 'H';
                break;
            case '\u00CC':
            case '\u00CD':
            case '\u00CE':
            case '\u00CF':
            case '\u0128':
            case '\u012A':
            case '\u012C':
            case '\u012E':
            case '\u0130':
            case '\u0132':
                letter = 'I';
                break;
            case '\u00EC':
            case '\u00ED':
            case '\u00EE':
            case '\u00EF':
            case '\u0129':
            case '\u012B':
            case '\u012D':
            case '\u012F':
            case '\u0131':
            case '\u0133':
                letter = 'i';
                break;
            case '\u0134':
                letter = 'J';
                break;
            case '\u0135':
                letter = 'j';
                break;
            case '\u0136':
                letter = 'K';
                break;
            case '\u0137':
            case '\u0138':
                letter = 'k';
                break;
            case '\u0139':
            case '\u013B':
            case '\u013D':
            case '\u013F':
            case '\u0141':
                letter = 'L';
                break;
            case '\u013A':
            case '\u013C':
            case '\u013E':
            case '\u0140':
            case '\u0142':
                letter = 'l';
                break;
            case '\u00D1':
            case '\u0143':
            case '\u0145':
            case '\u0147':
            case '\u014A':
                letter = 'N';
                break;
            case '\u00F1':
            case '\u0144':
            case '\u0146':
            case '\u0148':
            case '\u0149':
            case '\u014B':
                letter = 'n';
                break;
            case '\u00D2':
            case '\u00D3':
            case '\u00D4':
            case '\u00D5':
            case '\u00D6':
            case '\u014C':
            case '\u014E':
            case '\u0150':
            case '\u0152':
                letter = 'O';
                break;
            case '\u00F2':
            case '\u00F3':
            case '\u00F4':
            case '\u00F5':
            case '\u00F6':
            case '\u014D':
            case '\u014F':
            case '\u0151':
            case '\u0153':
                letter = 'o';
                break;
            case '\u0154':
            case '\u0156':
            case '\u0158':
                letter = 'R';
                break;
            case '\u0155':
            case '\u0157':
            case '\u0159':
                letter = 'r';
                break;
            case '\u015A':
            case '\u015C':
            case '\u015E':
            case '\u0160':
                letter = 'S';
                break;
            case '\u015B':
            case '\u015D':
            case '\u015F':
            case '\u0161':
                letter = 's';
                break;
            case '\u0162':
            case '\u0164':
            case '\u0166':
                letter = 'T';
                break;
            case '\u0163':
            case '\u0165':
            case '\u0167':
                letter = 't';
                break;
            case '\u00D9':
            case '\u00DA':
            case '\u00DB':
            case '\u00DC':
            case '\u0168':
            case '\u016A':
            case '\u016C':
            case '\u016E':
            case '\u0170':
            case '\u0172':
                letter = 'U';
                break;
            case '\u00F9':
            case '\u00FA':
            case '\u00FB':
            case '\u00FC':
            case '\u0169':
            case '\u016B':
            case '\u016D':
            case '\u016F':
            case '\u0171':
            case '\u0173':
                letter = 'u';
                break;
            case '\u0174':
                letter = 'W';
                break;
            case '\u0175':
                letter = 'w';
                break;
            case '\u00DD':
            case '\u0176':
            case '\u0178':
                letter = 'Y';
                break;
            case '\u00FD':
            case '\u00FF':
            case '\u0177':
                letter = 'y';
                break;
            case '\u0179':
            case '\u017B':
            case '\u017D':
                letter = 'Z';
                break;
            case '\u017A':
            case '\u017C':
            case '\u017E':
                letter = 'z';
                break;
        }

        return letter;
    }
}
