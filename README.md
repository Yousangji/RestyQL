# RestyQL
#### Query Interface for APIs

RestyQL is a query Interface for APIs benchmarking GraphQL and helps communicating between Client and back-end Server, solving the main problem of OOP.\
Regardless of whether you need it or not, a number of properties are structural problems that Object-Orient-Programming has.
It communicates with resources in form of object, Neither side client or server could select properties which they only require.\
'RestQL' is the solution to this problem by specifying and communicating what is needed.


## Feature
### 1. RestyQL is about setting or asking for specific fields required or restricted on object.
   For example, Server can set a range of fields on objects returning as a response, and at the same time specify some fields not to be sent with 'RestyQL annotation'. Client side can also ask for specific fields on objects and specify some fields to Ignore by adding 'query','ignore' parameter when requests http.

   ####Client Side Request 
  > `query = id, name, profile , contacts.email `   
   Client request value of id, email, profile and only email address of contact field.
     
      
  >`ignore = profile.location.let `\
   Client request not to send location value. profile is subclass which has location as one of fields.
            
            
            
     
  >         http://api/users?query = id,name,profile,contact.email & ignore = profile.location
   This query will return user objects which only contains id, name, profile class except location field, email field of contact object.
   
   
  
#### Server Side Setting
     @RestyQL( value = {"name", "profile", "contacts"}, ignore = { "id", "password", "profile.location.detail" } )
    
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
    {
    	"name" : {},
    	"profile" :
    		{
    			"sex" : {},
    			"hobby" : {},
    			"location" : {
    				"lan" : {}
    			    }
    		},
    	"contacts":
    		{
    			"email":{}
    		}
    }
    

### Quick Start


Step 1. Add the JitPack repository to your build file
```	
<repositories>
  <repository>  
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
Step 2. Add the dependency
```	
<dependency>
   <groupId>com.github.User</groupId>
   <artifactId>RestyQL</artifactId>
   <version>1.0.0-SNAPSHOT</version>
</dependency>
```
