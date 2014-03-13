/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * AgeCalculator
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class AgeCalculator {

    private static final String SAI = "歳";

    public static String getAgeAndBirthday(String mmlBirthday, int monthAge) {

        int[] spec = getAgeSpec(mmlBirthday);

        if (spec[0] != -1 && spec[1] != -1) {

            StringBuilder sb = new StringBuilder();
            sb.append(spec[0]);

            if (spec[0] < monthAge && spec[1] != 0) {
                sb.append(".").append(spec[1]);
            }

            sb.append(" ").append(SAI);
            sb.append(" (").append(mmlBirthday).append(")");
            return sb.toString();
        }
        return null;
    }

    public static String getAge(String mmlBirthday, int monthAge) {

        int[] spec = getAgeSpec(mmlBirthday);

        if (spec[0] != -1 && spec[1] != -1) {

            StringBuilder sb = new StringBuilder();
            sb.append(spec[0]);

            if (spec[0] < monthAge && spec[1] != 0) {
                sb.append(".").append(spec[1]);
            }
            return sb.toString();
        }
        return null;
    }

    public static int[] getAgeSpec(String mmlBirthday) {

        try {
            GregorianCalendar gc1 = getCalendar(mmlBirthday);
            GregorianCalendar gc2 = new GregorianCalendar(); // Today
            int years = 0;
            int month = 0;
            int days = 0;

            gc1.clear(Calendar.MILLISECOND);
            gc1.clear(Calendar.SECOND);
            gc1.clear(Calendar.MINUTE);
            gc1.clear(Calendar.HOUR_OF_DAY);

            gc2.clear(Calendar.MILLISECOND);
            gc2.clear(Calendar.SECOND);
            gc2.clear(Calendar.MINUTE);
            gc2.clear(Calendar.HOUR_OF_DAY);

            while (gc1.before(gc2)) {
                gc1.add(Calendar.YEAR, 1);
                years++;
            }

            gc1.add(Calendar.YEAR, -1);
            years--;

            while (gc1.before(gc2)) {
                gc1.add(Calendar.MONTH, 1);
                month++;
            }
            gc1.add(Calendar.MONTH, -1);
            month--;

            while (gc1.before(gc2)) {
                gc1.add(Calendar.DAY_OF_MONTH, 1);
                days++;
            }
            days--;

//s.oh^ 2013/06/10 月齢計算
            String[] birth = mmlBirthday.split("-");
            if (birth != null && birth.length == 3) {
                years = gc2.get(Calendar.YEAR) - Integer.parseInt(birth[0]);
                month = (gc2.get(Calendar.MONTH) + 1) - Integer.parseInt(birth[1]);
                days = gc2.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(birth[2]);
                if (days < 0) {
                    month = month - 1;
                }
                if (month < 0) {
                    years = years - 1;
                    month = month + 12;
                }
                int nowMonth = gc2.get(Calendar.MONTH) + 1;
                if (gc2.get(Calendar.DAY_OF_MONTH) > Integer.parseInt(birth[2])) {
                    days = gc2.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(birth[2]);
                } else if (gc2.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(birth[2])) {
                    days = 0;
                } else {
                    if (nowMonth == 2) {
                        if (gc2.get(Calendar.YEAR) % 4 == 0) {
                            if (gc2.get(Calendar.YEAR) % 400 == 0) {
                                days = 29 - Integer.parseInt(birth[2]);
                                days = days + gc2.get(Calendar.DAY_OF_MONTH);
                            } else if (gc2.get(Calendar.YEAR) % 100 == 0) {
                                days = 28 - Integer.parseInt(birth[2]);
                                days = days + gc2.get(Calendar.DAY_OF_MONTH);
                            } else {
                                days = 29 - Integer.parseInt(birth[2]);
                                days = days + gc2.get(Calendar.DAY_OF_MONTH);
                            }
                        } else {
                            days = 28 - Integer.parseInt(birth[2]);
                            days = days + gc2.get(Calendar.DAY_OF_MONTH);
                        }
                    } else if (nowMonth == 1 || nowMonth == 3 || nowMonth == 5 || nowMonth == 7 || nowMonth == 8 || nowMonth == 10 || nowMonth == 12) {
                        days = 31 - Integer.parseInt(birth[2]);
                        days = days + gc2.get(Calendar.DAY_OF_MONTH);
                    } else {
                        days = 30 - Integer.parseInt(birth[2]);
                        days = days + gc2.get(Calendar.DAY_OF_MONTH);
                    }
                }
            }
//s.oh$

            return new int[]{years, month, days};

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return new int[]{-1, -1, -1};
        }
    }

    public static GregorianCalendar getCalendar(String mmlDate) {

        try {
            // Trim time if contains
            mmlDate = trimTime(mmlDate);
            String[] cmp = mmlDate.split("-");
            String yearSt = cmp[0];
            String monthSt = cmp[1];
            if (monthSt.startsWith("0")) {
                monthSt = monthSt.substring(1);
            }
            String daySt = cmp[2];
            if (daySt.startsWith("0")) {
                daySt = daySt.substring(1);
            }
            int year = Integer.parseInt(yearSt);
            int month = Integer.parseInt(monthSt);
            month--;
            int day = Integer.parseInt(daySt);

            return new GregorianCalendar(year, month, day);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static String trimTime(String mmlDate) {

        if (mmlDate != null) {
            int index = mmlDate.indexOf('T');
            if (index > -1) {
                return mmlDate.substring(0, index);
            } else {
                return mmlDate;
            }
        }
        return null;
    }

//masuda^   和暦を含めた生年月日
    public static String getAgeAndBirthday2(String mmlBirthday, int monthAge) {

        String age = getAge(mmlBirthday, monthAge);
        if (age != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(age);
            sb.append(" ");
            sb.append(SAI);
            sb.append(" ([");
            sb.append(toNengo(mmlBirthday).substring(0, 3));
            sb.append("]");
            sb.append(mmlBirthday);
            sb.append(")");
            return sb.toString();
        }
        return null;
    }

    // 西暦=>年号変換 Dr pnus?
    public static String toNengo(String mmlBirthday) {
        int year;
        int month;
        int day;
        String nengo;

        year = Integer.valueOf(mmlBirthday.substring(0, 4));
        month = Integer.valueOf(mmlBirthday.substring(5, 7));
        day = Integer.valueOf(mmlBirthday.substring(8, 10));

        // 1990年より先は平成
        if (year >= 1990) {
            nengo = "H";
            year = year - 1988;
        } // 1989年だったら，1月7日以前は昭和
        else if (year == 1989) {
            if (month == 1 && day <= 7) {
                nengo = "S";
                year = 64;
            } else {
                nengo = "H";
                year = 1;
            }
        } // 1927年から1988年は昭和
        else if (year >= 1927 && year <= 1988) {
            nengo = "S";
            year = year - 1925;
        } // 1926年だったら，12月25日以降は昭和
        else if (year == 1926) {
            if (month == 12 && day >= 25) {
                nengo = "S";
                year = 1;
            } else {
                nengo = "T";
                year = 15;
            }
        } // 1913年から1925年は大正
        else if (year >= 1913 && year <= 1925) {
            nengo = "T";
            year = year - 1911;
        } // 1912 年だったら，7/30 以降は大正
        else if (year == 1912) {
            if (month >= 8) {
                nengo = "T";
                year = 1;
            } else if (month <= 6) {
                nengo = "M";
                year = 45;
            } else if (day >= 30) {
                nengo = "T";
                year = 1;
            } else {
                nengo = "M";
                year = 45;
            }
        } // 1911年以前は明治
        else {
            nengo = "M";
            year = year - 1867;
        }

        StringBuilder buf = new StringBuilder();
        buf.append(nengo);
        buf.append(String.format("%02d", year));
        buf.append("-");
        buf.append(String.format("%02d", month));
        buf.append("-");
        buf.append(String.format("%02d", day));
        return buf.toString();
    }

    // test(mmlDate形式)当時の年齢を計算する
    public static String getAge2(String mmlBirthday, String test) {

        try {
            GregorianCalendar gc1 = getCalendar(mmlBirthday);
            GregorianCalendar gc2 = getCalendar(test);
            int years = 0;

            gc1.clear(GregorianCalendar.MILLISECOND);
            gc1.clear(GregorianCalendar.SECOND);
            gc1.clear(GregorianCalendar.MINUTE);
            gc1.clear(GregorianCalendar.HOUR_OF_DAY);

            gc2.clear(GregorianCalendar.MILLISECOND);
            gc2.clear(GregorianCalendar.SECOND);
            gc2.clear(GregorianCalendar.MINUTE);
            gc2.clear(GregorianCalendar.HOUR_OF_DAY);

            while (gc1.before(gc2)) {
                gc1.add(GregorianCalendar.YEAR, 1);
                years++;
            }
            years--;

            int month = 12;

            while (gc1.after(gc2)) {
                gc1.add(GregorianCalendar.MONTH, -1);
                month--;
            }

            StringBuilder buf = new StringBuilder();
            buf.append(years);
            if (month != 0) {
                buf.append(".");
                buf.append(month);
            }
            return buf.toString();

        } catch (Exception e) {
            return null;
        }
    }
//masuda$
}
