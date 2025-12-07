public abstract class Person
{
    protected String name;
    protected int age;
    protected String email;
    protected String phoneNumber;
    
    public Person(String name, int age, String email, String phoneNumber)
    {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    public abstract void displayInfo();
    
    public String getName()
    {
        return name;
    }
}