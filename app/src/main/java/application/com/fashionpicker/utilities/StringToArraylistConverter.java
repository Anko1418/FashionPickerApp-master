package application.com.fashionpicker.utilities;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;

public class StringToArraylistConverter {

    @TypeConverter
    public String toString(ArrayList<String> arrayList) {

        try {

            if (arrayList.size() > 0) {

                StringBuilder result = new StringBuilder();
                for (int i = 0; i < arrayList.size(); i++) {

                    result.append(arrayList.get(i)).append("%%");
                }
                return result.toString();
            } else {

                return "";
            }
        } catch (Exception e) {

            return "";
        }
    }

    @TypeConverter
    public ArrayList<String> toArrayList(String s) {

        try {

            if (s.equals("")) {

                return new ArrayList<>();
            } else {

                String[] strings1 = s.split("%%");
                ArrayList<String> strings = new ArrayList<>();
                for (String str: strings1) {

                    if (!str.equals("")) {

                        strings.add(str);
                    }
                }
                return strings;
            }
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }
}
