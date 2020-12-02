package com.example.zhanbozhang.test.model;

import java.util.Comparator;
import java.util.function.Predicate;

public class Summary {

    public String year;

    public String semester;

    public String department;

    public long number;

    public String title;

    public String description;

    public Summary(String department, long number, String title) {
        this.department = department;
        this.number = number;
        this.title = title;
    }

    public static final Comparator<Summary> COMPARATOR = (mo1, mo2) -> {
        int cr = 0;
        //first: sorted by department
        int a = mo1.department.compareToIgnoreCase(mo2.department);

        if (a != 0) {
            cr = a > 0 ? 3 : -1;
        } else {
            //second: sorted by number
            a = (int) (mo1.number - mo2.number);

            if (a != 0) {
                cr = (a > 0) ? 2 : -2;
            } else {
                //third: sorted by title
                a = mo1.title.compareToIgnoreCase(mo2.title);
                if (a != 0) {
                    cr = (a > 0) ? 1 : -3;
                }
            }
        }

        //-3, -2, -1, 0, 1, 2, 3

        return cr;
    };

    public static final Predicate<Summary> PREDICATE = summary -> {
        return summary.number > 10;
    };

    @Override
    public String toString() {
        return "Summary{" +
                ", department='" + department + '\'' +
                ", number=" + number +
                ", title='" + title + '\'' +
                '}';
    }
}
