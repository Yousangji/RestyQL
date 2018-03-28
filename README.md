# RestyQL
### Alternative to GraphQL Fully Compatible to Standard REST API


1. Ask for What you want, Restricting What you do not want
2. Self-describable data with intact existing code.
3. Get Predictable Result Fast.
4. No need to Learn, No need to Modify.



Give the power of controlling data which they wants or do not want.\
The power is given to not only Client But Server.\
Result is always predictable because it only contains required values.\
Compacter results , Faster results. \
Adding annotation or query parameter is the only thing that developer should do.\
 

## For Example
### 1. RestyQL is about setting or asking for specific fields required or restricted on object.
   For example, Server can set a range of fields on objects returning as a response, and at the same time specify some fields not to be sent with 'RestyQL annotation'. Client side can also ask for specific fields on objects and specify some fields to Ignore by adding 'query','ignore' parameter when requests http.

   #### Client Side Request 
  >  `query = id, name, profile , contacts.email`   
  
   Client request value of id, email, profile and only email address of contact field.
     
      
  > `ignore = profile.location.let` 
  
   Client request not to send location value. profile is subclass which has location as one of fields.
            
            
            
     
  >     http://api/users?query = id,name,profile,contact.email & ignore = profile.location
   This query will return user objects which only contains id, name, profile class except location field, email field of contact object.
   
   
  
#### Server Side Setting
```java
public class UserController{
    
@RestyQL(value = {"name", "profile", "contacts"}, ignore = {"id", "password", "profile.location.detail"})
public User getUsers(){
    return User;
}

}
```
   
   Putting @RestyQL annotation on method,\
   Server specifies that 'name', 'profile', 'contacts' fields are only options and restricts 'id', 'password', 'profile.location.detail' fields not to be sent.
   
   
  
  
  
  
 &nbsp;
 &nbsp;
 &nbsp;
### 2. RestyQL helps http communication be self-documenting.
   Adding 'schema' parameter in http request, client would get empty schema of objects which describe the result of request.
    
       http://api/users?query = id,name,profile,contact.email & ignore = profile.location & schema
       
    
  > If any http request contains 'schema' as a key of parameter,\
   It will returns empty schema of objects filtered by client side request and server side setting.
  >
```json
{
    "name": {},
    "profile": {
        "sex": {},
        "hobby": {},
        "location": {
            "lan": {}
        }
    },
    "contacts": {
        "email": {}
    }
}
```
    

### Quick Start

Step 1. Add the JitPack repository to your build file
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Step 2. Add the dependency
```xml
<dependency>
    <groupId>com.github.Yousangji</groupId>
    <artifactId>RestyQL</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
