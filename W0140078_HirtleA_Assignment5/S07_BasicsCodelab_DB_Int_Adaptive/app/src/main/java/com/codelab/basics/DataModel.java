package com.codelab.basics;

// My simple data class for one Pokémon in the DB
// (POJO = plain old Java object)
public class DataModel {

    // fields = columns in my table
    private long id;            // DB primary key (helps with updates)
    private String name;        // Pokémon name
    private Integer number;     // Pokédex number
    private Integer powerLevel; // made-up power score for the assignment
    private String description; // short blurb about the Pokémon
    private Integer accessCount;// how many times I opened its details (used to find "favorite")

    // no-args constructor
    // some libraries (and my DB helper) like having this
    public DataModel() {
        this.id = 0L;          // default 0 (not saved yet)
        this.name = "Unknown"; // placeholder
        this.number = 0;       // placeholder
        this.powerLevel = 0;   // placeholder
        this.description = ""; // empty string is safer than null
        this.accessCount = 0;  // start at 0 views
    }

    // full constructor so I can quickly make a filled object
    // note: if accessCount is null, I set it to 0 to avoid crashes
    public DataModel(long id, String name, Integer number, Integer powerLevel, String description, Integer accessCount) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.powerLevel = powerLevel;
        this.description = description;
        this.accessCount = (accessCount == null) ? 0 : accessCount;
    }

    // when I print this object, show something readable
    // handy for logs or quick list items while testing
    @Override
    public String toString() {
        return "Pokémon #" + number + " " + name +
                "  PL=" + powerLevel +
                "  Access=" + accessCount +
                "\n" + description;
    }

    // getters/setters = standard way to read/write the fields
    // DB code will use these to map from rows <-> objects
    // UI code will use these to show things on screen

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public Integer getPowerLevel() { return powerLevel; }
    public void setPowerLevel(Integer powerLevel) { this.powerLevel = powerLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAccessCount() { return accessCount; }
    public void setAccessCount(Integer accessCount) { this.accessCount = accessCount; }
}
