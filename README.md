# Idempotence DSL for kotlin (WIP)

> This project just started and is in initial definitions and implementations, if you have some suggestion feel free to send me a message or to even open a Pull Request.

### Objectives

This project has as main objective create a simple idempotence processor to be defined through kotlin DSL.

For example:

```kotlin
 
class YourService (idempotenceManager: IdempotenceManager){
    
    fun yourFunction() : YourReturn =
      idempotenceManager.execute( // idempotence manager that manages the idempotence to you
          idempotentProcess<YourReturn>( // the idempotence DSL
            idempotenceKey, // your idempotence key
            "yourFunction" // the idempotence collection key (like group) to be used to index idempotents registers
          ) {
           
            acceptRetry(true) // If the first execution fails and a second one comes, the main function will be executed again
  
            execute { // then main function
              // your business logic to be executed once
              YourReturn()
            }
  
            onAlreadyExecuted {
              // to be executed when already executed
              // normally just returns you persisted data to your client
              YourReturn()
            }
  
            onError {
              // runned every time you have error in your main function
            }
          }
      )
    
}

 

)
```

### Knowed limitations:

- If a second request comes and the first one still processing, the system will throw a `LockUnavailableException`
  - This is a limitation or a expected behavior? We should add a possibility to configure the process to wait the first execution finished to return the response??? Oõ