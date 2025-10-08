package com.codelab.basics;

import java.util.List;

/*
 * This is just a "contract" (interface) for my Pokémon DB.
 * It says what functions exist, not how they work inside.
 * The real code lives in a class that implements this (e.g., DBClass).
 */
public interface DB_Interface {

    // return how many rows are in the table (useful for quick checks)
    int count();

    // save a new Pokémon row to the DB
    // returns 1 if it worked, 0 if it failed
    int save(DataModel dataModel);

    // update an existing Pokémon row (matched by its id inside dataModel)
    // returns how many rows got changed (should be 1 if it worked)
    int update(DataModel dataModel);

    // delete one row by id
    // returns number of rows removed (1 if success, 0 if not found)
    int deleteById(Long id);

    // get every row in the table, ordered by id (smallest -> biggest)
    List<DataModel> findAll();

    // quick helper: just give me the name for this id (or null if not there)
    String getNameById(Long id);

    // optional helper: return the Pokémon with the highest powerLevel
    // (can be null if the table is empty)
    DataModel getMax();

    // add 1 to accessCount for this Pokémon (used to track "favorite")
    void incAccessCount(long id);

    // return the id of the Pokémon that has the highest accessCount
    // if table is empty, return 0 (means "none")
    long getMostAccessed();

    // fetch a single Pokémon by id (or null if not found)
    DataModel getById(long id);
}
