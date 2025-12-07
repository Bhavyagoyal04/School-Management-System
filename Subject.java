enum Subject
{
    MATHEMATICS("Math", 4), 
    SCIENCE("Science", 5), 
    ENGLISH("English", 3), 
    HISTORY("History", 3), 
    COMPUTER_SCIENCE("Computer Science", 5);
    
    private final String displayName;
    private final int creditHours;
    
    Subject(String displayName, int creditHours)
    {
        this.displayName = displayName;
        this.creditHours = creditHours;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public int getCreditHours()
    {
        return creditHours;
    }
    
    public static Subject fromDisplayName(String name)
    {
        for (Subject subject : values())
        {
            if (subject.getDisplayName().equals(name))
            {
                return subject;
            }
        }
        return MATHEMATICS;
    }
}