# Idempotence DSL for kotlin (WIP)

> This project just started and is in initial definitions and implementations, if you have some suggestion feel free to send me a message or to even open a Pull Request.

### Objectives

This project has as main objective create a simple idempotence processor to be defined through kotlin DSL.

For example:

```
 
 idempotenceManager.execute(
     
     idempotentProcess<YourFunctionReturnType>(idempotenceKey, "GroupKey") {
        acceptRetry(true) // If the first execution fails and a second one comes, the main function will be executed again
        
        execute { // main function
            // execute your idempotent process here
        }
        
        onAlreadyExecuted {
            // to be executed when already executed
            // normally just returns you persisted data to your client
        }
        
        onError {
            // runned every time you have error in your main function
        }
    }

)
```

### Knowed limitations:

- If a second request comes and the first one still processing, the system will throw a `LockUnavailableException`
  - This is a limitation or a expected behavior? We should add a possibility to configure the process to wait the first execution finished to return the response??? Oõ