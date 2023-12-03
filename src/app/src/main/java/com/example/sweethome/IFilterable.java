package com.example.sweethome;

/* necessary imports */
import com.google.firebase.Timestamp;

/**
 * @interface IFilterable
 *
 * <p>An interface for filtering. Contains method signatures for
 * filtering by date, keyword, make, and tags. </p>
 *
 * @date <p>November 10, 2023</p>
 */
public interface IFilterable {
    public void filterByDate(Timestamp startDate, Timestamp endDate);
    public void filterByKeyword(String keyword);
    public void filterByMake(String make);
    public void filterByTag();
}
