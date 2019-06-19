# Description

In honor of Detriots startup week we have taking the liberty of implementing a basic app, that aspiring engineers and entrepreneur can use as a source of knowledge and inspiration for their own projects. This project has been accommodated to run on both the Android and iOS platforms, while maintaining a shared custom backend implented via Google Firebase. 

The project we have decided to create is a simple reminder application. Throughout the course of this README we will be explaining how to: implement email and password authentication using Firebase/Auth, reading and writing to a NoSQL database using Firebase/Database, and how to construct relationships between models in JSON.

<a href= "#Android">Click here for Android</a>


<a href= "#IOS">Click here for IOS</a>

# iOS

## Contents

1. [Creating A New XCode Project](#newProject) 
2. [Setting Up Firebase](#firebaseSetup)
3. [Installing Firebase Using CocoaPods](#cocoapods)
     - [Installing CocoaPods](#cocoapodsInstallation)
     - [Importing Firebase Into Our Project](#importFirebase)
     - [Configuring Firebase Into Our App](#firebaseConfig)
4. [Authenticating Users With Firebase](#authentication)
     - [Creating A New User](#createUser)
     - [Logging In With An Existing User](#login)
5. [Interacting With The Firebase Database](#database)
     - [Writing A User To The Database](#savingUser)
     - [Reading In A User From The Database](#fetchingUser)
     - [Writing A Reminder To The Database](#savingReminder)
     - [Reading In A List Of Reminders From The Database](#readingReminders)
6. [Conclusion](#conclusion)

<a name="newProject"> Creating A New Xcode Project </a>
--------------
If you don't already have Xcode installed on your computer you can install directly from the Mac AppStore. Once you open the application you will be promted with the following screen.

<img width="500" alt="Screen Shot 2019-06-15 at 8 39 48 PM" src="https://user-images.githubusercontent.com/22037563/59557675-d9574480-8fad-11e9-8943-cce73fac8c53.png">

Go ahead and click `Create a new Xcode project` and select the `Single view app` is selected under the `Application` section. You will then be prompted to name your application, and perhaps for an organization name and/or identifer. You can use anything in pretty anything you want for these fields. Tap next and congratulations you just created an Xcode project!

<a name="firebaseSetup"> Setting Up Firebase </a>
--------------

We will want to create a Firbase project before we get too ahead of ourselves. Head over to https://firebase.google.com/ and in the top right hand corner click on the `Go to console` button (You will need to be signed into a google account to access the console). From here we can tap `Create a project`, enter in a name for the project, accept and agree to the `controller-controller` and `applicable` terms and tap `Create project`.

We will now to add an iOS app to our project. In the project overview section tap the iOS button; here you will be prompted for a bundle identifier. Open xcode back up and in the Project Navigator (Folder icon in the top left hand corner) and tap the blue project icon. This will expose you Xcode projects bundle identifier, copy it and paste it in the `iOS bundle ID` field in Firebase and tap `Register App`. 

Next download the `GoogleService-Info.plist` and drag and drop it into the Xcode project underneath the current `Info.plist` file. Congratulations you just successfully created a Firebase project and registered your iOS app!

<a name="cocoapods"> Installing Firebase Using Cocoapods </a>
--------------

### <a name=cocoapodsInstallation> Installing CocoaPods </a>

You may be asking yourself what is CocoaPods? CocoaPods is a dependency manager for Swift and Objective-C projects. A dependency manager is a tool that allows to import external software libraries into your project; if your a curious to learn more about what CocoaPods is see [here](https://www.youtube.com/watch?v=7nojXzgrlNU).  

To install CocoaPods open `Terminal` and type the following command:
```
sudo gem install cocoapods
```
If you are having trouble installing CocoaPods see [here](https://stackoverflow.com/questions/20755044/how-to-install-cocoapods) for potential troubleshooting solutions.

### <a name="importFirebase"> Importing Firebase Into Our Project </a>

Now to add Firebase to our project, while still in Terminal navigate to your Xcode project's directory using the `cd` command here is an example of how to do this. Say your project is name `StarterProject` and you placed it on your Desktop to navigate into the directory you use the following commands:
```
cd ~
cd Desktop
cd StarterProject
```

once there run `pod init`. This will create a Podfile for us to integrate the libraries whaat we need. Open up the Podfile which should now be in the project folder and add the following snippet under the `# Pods for .....` line. 
```
  pod 'Firebase'
  pod 'Firebase/Auth'
  pod 'Firebase/Database'
```

Save the file and back in `Terminal` run `pod install`, this will generate a `.xcworkspace` file for us. Close the current instance of Xcode if you haven't already and reopen Xcode from the `.xcworkspace` file. This is were we will be creating and editing our project.

### <a name="firebaseConfig"> Configuring Firebase Into Our App </a>

Now we will need to configure a FirebaseApp shared instance. Open up `AppDelegate.swift` and update the code to look like so: 
```swift
import UIKit
import Firebase

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        FirebaseApp.configure()
        return true
    }
```

You're now ready to code!

<a name="authentication"> Authenticating Users With Firebase </a>
--------------

### <a name="createUser"> Creating A New User </a>

The first thing we will need to do is create a `User` [model](https://cocoacasts.com/model-view-viewmodel-in-swift) to do so create a new file name `User.swift` and add the following code to the file.

```swift
struct User: Equatable {
    var uid: String?
    var email: String?
    var firstName: String?
    var lastName: String?
}
```

If we wanted to we could hold a reference to the user that will persist inbetween app launches like so

```swift
extension User {
    func saveToDefaults() {
        UserDefaults.standard.set(self.uid, forKey: "me")
    }
}
```

The code below is a wrapper used to create a new user. In this project this codes lives in `AuthenticationManager.swift` which is a [Singleton](https://medium.com/if-let-swift-programming/the-swift-singleton-pattern-442124479b19) that handles/manage all functions and members related to authentication.

```swift
    func signUp(with email: String?, password: String?, firstName: String, lastName: String, completion: @escaping (User?, Error?) -> ()) {
        /// unwrap email and password
        guard let email = email,
            let password = password else {
                return
        }
        
        /// Create a new Firebase user using email and password
        Auth.auth().createUser(withEmail: email, password: password) { [weak self] result, error in
            if let err = error {
                print("Error Signing In: \(err.localizedDescription)")
                completion(nil, err)
                return
            }
            
            guard let result = result else {
                print("Error Signing In No User Data")
                completion(nil, nil)
                return
            }
            
            let user = User(uid: result.user.uid, email: email, firstName: firstName, lastName: lastName)
                       
            /// save reference of user to UserDefaults           
            user?.saveToDefaults()
            
            /// set shared instance user to newly created user
            self?.user = user
            completion(user, nil)
        }
    }
```
The snippet above is a function that takes in 4 strings, one for an email, password, first name, and last name. It also takes in a [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4).

So if we have a valid email, and password we use the Firebase `Auth.auth().createUser(withEmail:,password:,completion:)` method, within the callback of the `createUser` method we check to see if an error was return. If there was an error we pass it along to our completion handler. Otherwise we unwrap the result, create a user with that result and pass the user to our completion handler instead.

### <a name="login"> Logging In With An Existing User </a>

Logging in with an existing user is pretty similar to creating a new user. The code below is a wrapper for logging in an existing user

```swift
func login(with email: String?, password: String?, completion: @escaping (User?, Error?) -> ()) {
        /// unwrap email and password
        guard let email = email,
            let password = password else {
                return
        }
        
        /// Sign in with an existing Firebase user using email and password
        Auth.auth().signIn(withEmail: email, password: password) { [weak self] result, error in
            if let err = error {
                print("Error Signing In: \(err.localizedDescription)")
                completion(nil, err)
                return
            }
            
            guard let result = result else {
                print("Error Signing In No User Data")
                completion(nil, nil)
                return
            }
            
            /// Fetch the user data that is save in the database
            DatabaseManager.shared.fetchUser(with: result.user.uid, completion: { [weak self] user, error in
                if let err = error {
                    completion(nil, err)
                    return
                }
                
                /// save reference of user to UserDefaults
                user?.saveToDefaults()
                
                /// set shared instance user to newly created user
                self?.user = user
                completion(user, nil)
            })
        }
    }
```

As you can see the format is exactly the same the only difference here is that instead of sending up first and last name data we must now fetch that data. We can do so by using the [`DatabaseManager.fetchUser(with:completion:)`](#fetchUser)

<a name="database"> Interacting With The Firebase Database </a> 
--------------

All methods and members relating to the database are on the `DatabaseManager` [Singleton](https://medium.com/if-let-swift-programming/the-swift-singleton-pattern-442124479b19) class. We will need to key into specific nodes of our database to extract specific data so we will create an [`enum`](https://medium.com/@abhimuralidharan/enums-in-swift-9d792b728835) that looks like so

```swift
enum Nodes: String {
    case users = "Users"
    case reminders = "Reminders"
}
```
we will be using this `enum` to key into our uppermost level nodes.

Now we need to set up the database in our Firebase console. In the side menu on the left select the `Database` option. There are two database options in Firebase `Cloud Firestore` and `Realtime Database`, in this project we are using the `Realtime Database` so make sure when you create your database that you are using the `Realtime Database option`. I would also recommend to choose `Start in test mode`, but it's not mandataory since we are authenticating our users.
 
### <a name="savingUser"> Writing A User To The Database </a>

When [creating a new user](#createUser) we will want write that user to our database. We can leverage the Firebase `Database.database().setValue(_:)` method to send up the necessary values. We can't simply send up our models we will need to map our `User` to a [`Dictionary<String, Any>`](https://www.tutorialspoint.com/swift/swift_dictionaries.htm) so that we can properly write our user to the database. To do so we can create this helper on user

```swift
extension User {
    func toDictionary() -> [String : Any] {
        return ["email" : email ?? "",
                "firstName" : firstName ?? "",
                "lastName" : lastName ?? ""]
    }
}
```

Now that we have a way to convert our User [model](https://cocoacasts.com/model-view-viewmodel-in-swift) to a [`Dictionary<String, Any>`]((https://www.tutorialspoint.com/swift/swift_dictionaries.htm)) we can now see how we will save our user to that database, but before we look at the code lets consider how we want the data to be structed in our database.

We will need a node to hold reference of where all of our users are stored. Next we will need a way to identify each individual user; we can use a unique identifer for this. And lastly we will need to save the user information. Base on this criteria our data should look like so:

![Screen Shot 2019-06-16 at 6 08 03 PM](https://user-images.githubusercontent.com/22037563/59570162-d9188100-9061-11e9-87d7-fd57f1fd2b01.png)


Now lets look at the code needed to write our user up to the database while maintaining the structure above

```swift
    func putUser(user: User?, completion: @escaping (Error?) -> ()) {
        guard let user = user,
            let uid = user.uid else {
            return
        }
        
        Database.database().reference().child(Nodes.users.rawValue).child(uid).setValue(user.toDictionary()) { (error, _) in
            completion(error)
        }
    }
```
This function calls into `Database.database().reference()` which is defined in the `GoogleService-Info.plist` file we imported earlier. Next we will key into the `Users` node  by using `.child(Nodes.users.rawValue)` (`Nodes.users.rawValue` is the same as saying `"Users"`), followed by keying into the users unique id node using `.child(uid)` and set the value using our [`Dictionary<String, Any>`](https://www.tutorialspoint.com/swift/swift_dictionaries.htm) representation. If an error is return then we will just pass it into our [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4).

### <a name="fetchingUser"> Reading In A User From The Database </a>

When reading in a value from the database using any of the FireBase `Database.database().observe(of:with:)` methods use a [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4) in which the callback returns a Firebase `DataSnapshot`, so we will need to a way to transform the `Datasnapshot` into our user model. We can do somthin similar to the following:

```swift
extension DataSnapshot {
    func toUser() -> User? {
        /// cast value to Dictionary<String, Any>
        guard let dict = value as? [String : Any] else {
            return nil
        }
        
        /// grab values from the dictionary
        let email = dict["email"] as? String
        let firstName = dict["firstName"] as? String
        let lastName = dict["lastName"] as? String
        
        /// create and return a new instance of User
        return User(uid: key,
                    email: email,
                    firstName: firstName,
                    lastName: lastName)
    }
}
```

Now that we have a way of transforming `DataSnapshot` into a `User` we can now take a look and see how we can leverage `Database.database().observe(of:with:)` and the newly add `DataSnapshot.toUser()` functions to fetch a user from the database.

```swift
    func fetchUser(with uid: String?, completion: @escaping (User?, Error?) -> ()) {
        guard let uid = uid else {
            completion(nil, nil)
            return
        }
        
        Database.database().reference().child(Nodes.users.rawValue).child(uid).observeSingleEvent(of: .value, with: { snapshot in
            completion(snapshot.toUser(), nil)
        }) { error in
            completion(nil, error)
        }
    }
```

This method takes in a string representing the users unique id and a [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4). This function calls into `Database.database().reference()`. Next we will key into the `Users` node  by using `.child(Nodes.users.rawValue)`, followed by keying into the users unique id node using `.child(uid)` and use the Firebase `Database.database().observeSingleEvent(of:with:withCancel:)` method to read in the values for a user. 

### <a name="savingReminder"> Writing A Reminder To The Database </a>

Before we can write a `Reminder` reminder we must first create the corresponding data type. Create a new file and name it `Reminder.swift` and add the following code to the new file.

```swift
struct Reminder: Equatable {
    var id: String
    var title: String?
    var description: String?
    var timestamp: TimeInterval
    var isComplete: Bool
}
```
You may be wondering what the `timestamp` and member is for. When we are fetching our data from Firebase it comes back asynchronously which essentially means that the data that comes back may not be in the same order as it was before, so we can use the timestamp member to sort the data based on the most recent reminder add

Now that we have our [model](https://cocoacasts.com/model-view-viewmodel-in-swift) setup we will need a way to cast this to a [`Dictionary<String, Any>`](https://www.tutorialspoint.com/swift/swift_dictionaries.htm). So we can use the following helper function to achieve this.

```swift
extension Reminder {
    func toDictionary() -> [String : Any] {
        return ["title" : title ?? "",
                "description" : description ?? "",
                "timestamp" : timestamp,
                "isComplete" : isComplete]
    }
}
```

Before we write the code for this lets take a step back and think about how we want this data to be structured in our database. We will have a top level node called "Reminders". Now each user will have their own list of reminders so we can create another node keyed by the user's unique identifier. Each reminder will need it's own node so we can achieve keying the node by the reminder's id, and finally we can set the reminders data. This should look like so

![Screen Shot 2019-06-16 at 7 33 52 PM](https://user-images.githubusercontent.com/22037563/59570971-b2f8de00-906d-11e9-892e-d3e1c653bdf3.png)

Let's see how this will look in code 

```swift
    func putReminder(reminder: Reminder?, title: String, description: String?, completion: @escaping (Error?) -> ()) {
        guard let uid = AuthenticationManager.shared.user?.uid else {
            return
        }
        
        let uuid = reminder?.id ?? UUID().uuidString
        let timestamp = reminder?.timestamp ?? Date().timeIntervalSince1970
        let isComplete = reminder?.isComplete ?? false
        let reminder = Reminder(id: uuid, title: title, description: description, timestamp: timestamp, isComplete: isComplete)
        
        Database.database().reference().child(Nodes.reminders.rawValue).child(uid).child(uuid).setValue(reminder.toDictionary()) { (error, reference) in
            completion(error)
        }
    }
```

This method takes in an [optional](https://medium.com/@agoiabeladeyemi/optionals-in-swift-2b141f12f870) `Reminder`, 2 strings, and a [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4). We check to see if the current user is authenticated, then if the reminder passed in is not `nil` we can update the `title`, `description`, and `isComplete` members of the reminder. Otherwise we create a new uniquer identifier (`UUID`), set the timestamp to a the number of seconds that have passed since 1970, and set isComplete to false. We then key into the `Reminders` node of the database, next we key into the node corresponding to the current users uid, and then the node corresponding to the new reminders id; we then set the reminders values. If an error is return then we will just pass it into our [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4).

### <a name="readingReminders"> Reading In A List Of Reminders From The Database </a>

Before we can read in reminders we must handle transforming a `DataSnapshot` into a `Reminder` this will differ from the way we pull in a `User`. The reason being is that we are going to key in the current users uid node that is nested inside the "Reminders" node, meaning that there are potentially more node inside (a.k.a children). But first we need to be able to transform a `DataSnapshot` into a `Reminder`

```swift
extension DataSnapshot {
    func toReminder() -> Reminder? {
        guard let dict = self.value as? [String : Any] else {
            return nil
        }
        
        let title = dict["title"] as? String
        let description = dict["description"] as? String
        let timestamp = dict["timestamp"] as? TimeInterval ?? Date().timeIntervalSince1970
        let isComplete = dict["isComplete"] as? Bool ?? false
        
        return Reminder(id: key,
                        title: title,
                        description: description,
                        timestamp: timestamp,
                        isComplete: isComplete)
    }
}
```

Now that we have our transformer set up, lets see how to read these in and parse these children from the database

```swift
    func fetchCurrentUserReminders(completion: @escaping ([Reminder]?, Error?) -> ()) {
        guard let uid = AuthenticationManager.shared.user?.uid else {
            return
        }
        
        Database.database().reference().child(Nodes.reminders.rawValue).child(uid).observeSingleEvent(of: .value, with: { snapshot in
            guard let children = snapshot.children.allObjects as? [DataSnapshot] else {
                return
            }
            
            var reminders = [Reminder]()
            
            for child in children {
                guard let reminder = child.toReminder() else {
                    continue
                }
                reminders.append(reminder)
            }
            
            reminders = reminders.sorted(by: { lhs, rhs -> Bool in
                return lhs.timestamp > rhs.timestamp
            })
            
            completion(reminders, nil)
        }) { error in
            completion(nil, error)
        }
    }
```

So first we check to see if the current user is authenicated. Then we key into the "Reminders" node of the database, followed by keying into node corresponding to the users uid. We can now leverage the Firebase `Database.database().observeSingEvent(of:with:withCancel:)` method to read in all the users reminders. So we cast our `snapshot.children.allObjects` to an [array](https://www.tutorialspoint.com/swift/swift_arrays.htm) to an array of `DataSnapshot`. We can now [iterate](https://www.hackingwithswift.com/articles/76/how-to-loop-over-arrays) over that array and append each instance of transformed instance of `Reminder` to our `Reminders` [array](https://www.tutorialspoint.com/swift/swift_arrays.htm) but before we can pass this into our [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4) we must sort the array base on `timestamp` we can do so by using the [`Array.sorted(by:)`](https://developer.apple.com/documentation/swift/array/2296815-sorted) method. If an error is return then we will just pass it into our [completion handler](https://blog.bobthedeveloper.io/completion-handlers-in-swift-with-bob-6a2a1a854dc4).


# Android

## Contents

1. [Creating A New Android Studio Project](#newASProject) 
2. [Setting Up Firebase](#firebaseSetupAndroid)
3. [Importing Firebase Into Our Project](#importFirebaseAndroid)
     - [Configuring Firebase Into Our App](#firebaseConfigAndroid)
4. [Authenticating Users With Firebase](#authenticationAndroid)
     - [Creating A New User](#createUserAndroid)
     - [Logging In With An Existing User](#loginAndroid)
5. [Interacting With The Firebase Database](#databaseAndroid)
     - [Writing A User To The Database](#savingUserAndroid)
     - [Reading In A User From The Database](#fetchingUserAndroid)
     - [Writing A Reminder To The Database](#savingRemindeAndroidr)
     - [Reading In A List Of Reminders From The Database](#reading/writingRemindersAndroid)
6. [Conclusion](#conclusion)

<a name="newASProject"> Creating A New Android Studio Project </a>
--------------
If you don't already have Android Studio installed on your computer you can install it from <a href="https://developer.android.com/studio" >Here</a>. Once you open the IDE you will be prompted with the following screen. This workshop will be a demo of MVVM architecture observing changes to data. We will be using Kotlin throughout this workshop. Android Apps _can_ be made in Java but most companies including Google themselves are pushing away from Java.

<img width="500" alt="Screen Shot 2019-06-15 at 8 39 48 PM" src="https://user-images.githubusercontent.com/24880401/59791980-39e1cc80-92a1-11e9-89a5-46611a0b9644.PNG">

Go ahead and click `File -> New ->New Project` and select the `Basic Activity`. You will then be prompted to name your application, and perhaps for an organization name and/or identifier. Tap next and congratulations you just created an Android Studio project!

<a name="firebaseSetupAndroid"> Setting Up Firebase </a>
--------------

We will want to create a Firbase project before we get too ahead of ourselves. Head over to https://firebase.google.com/ and in the top right hand corner click on the `Go to console` button (You will need to be signed into a google account to access the console). From here we can tap `Create a project`, enter in a name for the project, accept and agree to the `controller-controller` and `applicable` terms and tap `Create project`.

We will now to add an Android app to our project. In the project overview section tap the Android button; here you will be prompted for a bundle identifier. Open Android Studio back up, head over to your App modules `build.gradle`. Here you will see an `applicationId`. Grab that and drop it into Firebase 

Next download the `google-services.json` and drag and drop it into the Android project in `App -> src`. Pop back over to the Firebase console and finish the prompted setup. Congratulations! You have just successfully created a Firebase project and registered your Android app!

### <a name="importFirebaseAndroid"> Importing Firebase Into Our Project </a>

Now to add Firebase to our dependencies. Add this snippet below to your projects `build.gradle`.

```
dependencies { 
 classpath 'com.google.gms:google-services:4.2.0' 
 …
 }
```

After this, add the snippet below to your App's `build.gradle` file.

```
dependencies { 
...
implementation 'com.google.firebase:firebase-core:17.0.0' 
implementation 'com.google.firebase:firebase-auth:18.0.0' 
implementation 'com.google.android.gms:play-services-auth:17.0.0' 
implementation 'com.google.firebase:firebase-database:18.0.0' 
 …
 }
 ```


### <a name="firebaseConfigAndroid"> Configuring Firebase Into Our App </a>

For simplicity we will create some global instances of the Firebase DB and Firebase Auth. This will keep the amount of
```kotlin
val auth = FirebaseAuth.getInstance()
val db = FirebaseDatabase.getInstance()
```

You're now ready to code!

<a name="authenticationAndroid"> Authenticating Users With Firebase </a>
--------------

### <a name="createUserAndroid"> Creating A New User </a>

The first thing we will need to do is create a `User` Model to do so create a new kotlin file named `User` and add the following code to the file.

```kotlin
data class User ( 
val uid: String? = "",
val firstName: String? = "", 
val lastName: String? = "", 
val email: String? = "" 
)
```

If we wanted to we could hold a reference to the user that will persist between app launches we have to store this `user.uid` in Shared Preferences. Thank can be done by calling the snippet below. I separated this into it's own SharedPrefsManager class to make it easier to save this from other places in the app. For example the Login/Signup screens. The `getString` and `putString` methods are wrapped in functions.

**Storing to Prefs:**
```kotlin

private var prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
 
prefs.getString(SharedPrefsKeys.USER_UID, "").orEmpty() 
prefs.edit().putString(SharedPrefsKeys.USER_UID, userId).apply()

```

This is what my manager class mentioned looks like. 

**SharedPrefsManager:**
```kotlin
open class SharedPrefsManager(activity: Context) { 
private var prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity) 

fun getCurrentUser() = prefs.getString(SharedPrefsKeys.USER_UID, "").orEmpty() 

fun setCurrentUser(userId: String) { try { prefs.edit().putString(SharedPrefsKeys.USER_UID, userId).apply() } catch (e: Exception) { } } }

```

### <a name="loginAndroid"> Logging In With An Existing User </a>

Logging in with an existing user is pretty similar to creating a new user. The code below is a wrapper for logging in an existing user

```kotlin
fun login(email: String, password: String) { 
if (email.isNotEmpty() && password.isNotEmpty()) { 
updateState(
 LoginViewState( 
progressType = ProgressType.Loading, 
isValidated = currentViewState().isValidated, 
userUID = currentViewState().userUID 
) ) 

auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { 
if (it.isSuccessful) { 

// Assert Non null here because we had a 
// successful login as per result. **There are better ways** 
val authUser = auth.currentUser!!

db.reference.child("Users").child(authUser.uid).addListenerForSingleValueEvent( object : ValueEventListener { 
override fun onDataChange(dataSnapshot: DataSnapshot) { 

val data = dataSnapshot.getValue(User::class.java) 

if(data != null) { 
user = user?.copy( 
uid = authUser.uid, 
email = data.email, 
firstName = data.firstName, 
lastName = data.lastName 
) } 

updateState( 
LoginViewState( 
progressType = ProgressType.Result, 
isValidated = currentViewState().isValidated, 
userUID = authUser.uid 
) ) } 

override fun onCancelled(databaseError: DatabaseError) { 
updateState( 
LoginViewState( 
progressType = ProgressType.Failure, 
isValidated = currentViewState().isValidated, 
userUID = currentViewState().userUID ) ) } } 
) } else { 
updateState( 
LoginViewState( 
progressType = ProgressType.Failure, 
isValidated = currentViewState().isValidated, 
userUID = currentViewState().userUID ) ) } }
} else { 
updateState( 
LoginViewState( 
progressType = ProgressType.Failure, 
isValidated = currentViewState().isValidated, 
userUID = currentViewState().userUID ) ) } 
}
```

As you can see the format is close to the same but here is that instead of sending up first and last name data we must now fetch that data. We can do so by using the [`db.reference.child("Users").child(savedUID)`](#fetchUser)

<a name="databaseAndroid"> Interacting With The Firebase Database </a> 
--------------
 
### <a name="savingUserAndroid"> Writing A User To The Database </a>

The code below is used to create a new user. In this project this codes lives in `SignUpViewModel.kt` which is a class that will handle the updates and changes to the view. This class will keep track of state and typically network calls are separated out into layers that this class talks to.

**Creating a new User:**
```kotlin
    
fun createNewUser(email: String, password: String, firstName: String, lastName: String) {

if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) { 
updateState( 
SignUpViewState( 
progressType = ProgressType.Loading,
isValidated = currentViewState().isValidated )
)

 auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { 
if (it.isSuccessful) { 

// Assert Non null here because we had a 
// successful login as per result. **There are better ways** 
val user = auth.currentUser!! 

db.reference.child("Users").child(user.uid).setValue( 
hashMapOf<String, Any>( 
Pair("email", email), 
Pair("firstName", firstName), 
Pair("lastName", lastName) ) 
) 

updateState( 
SignUpViewState( 
progressType = ProgressType.Result, 
isValidated = currentViewState().isValidated ) 
)
} else { 
updateState( 
SignUpViewState( 
progressType = ProgressType.Failure, 
isValidated = currentViewState().isValidated ) ) }
}} else { 
updateState( 
SignUpViewState( 
progressType = ProgressType.Failure, 
isValidated = currentViewState().isValidated ) ) } 
}
```
The snippet above is a function that takes in 4 strings, one for an email, password, first name, and last name.

So if we have a valid email, and password we use the Firebase ` auth.createUserWithEmailAndPassword(email, password)` method, within the callback (`addOnCompleteListener`) of the `createNewUser` method we check to see if an error was return. If there was an error we pass it along to our View State. You will notice that right now simple checks control our validation of input but we can have that live in the View Modle and persist through the View State as well. Currently I left the `isValidated` variable in but it is unused. Otherwise we unwrap the result, create a user with that result and pass the user to the next screen. **Something to consider not mentioned here is storing the `uid` of the user. Currently if the user exits the app after creating they will still have to restart and login.



### <a name="fetchingUserAndroid"> Reading In A User From The Database </a>

**Fetching User:** 
```kotlin
fun fetchUser(savedUID: String) { 
db.reference.child("Users").child(savedUID).addListenerForSingleValueEvent( object : ValueEventListener { 
override fun onDataChange(dataSnapshot: DataSnapshot) { 
val data = dataSnapshot.getValue(User::class.java) 

if (data != null) { 
user = user?.copy( 
uid = savedUID, 
email = data.email, 
firstName = data.firstName, 
lastName = data.lastName ) 
} 

mainProgressBar.visibility = GONE addFragmentToActivity(supportFragmentManager, ReminderView(), R.id.mainActivity) 
} 
override fun onCancelled(databaseError: DatabaseError) { } 
})
}
```

### <a name="savingReminderAndroid"> Writing A Reminder To The Database </a>

Before we can write a `Reminder` reminder we must first create the corresponding data type. Create a new file and name it `Reminder.kt` and add the following code to the new file.

```kotlin
data class Reminder ( 
var id : String? = null, 
var title : String? = "", 
var description : String? = "", 
var isComplete : Boolean? = false, 
var timestamp: Double? = null 
)
```
You may be wondering what the `timestamp` and member is for. When we are fetching our data from Firebase it comes back asynchronously which essentially means that the data that comes back may not be in the same order as it was before, so we can use the timestamp member to sort the data.

Before we write the code for this lets take a step back and think about how we want this data to be structured in our database. We will have a top level node called "Reminders". Now each user will have their own list of reminders so we can create another node keyed by the user's unique identifier. Each reminder will need it's own node so we can achieve keying the node by the reminder's id, and finally we can set the reminders data. This should look like so

![Screen Shot 2019-06-16 at 7 33 52 PM](https://user-images.githubusercontent.com/22037563/59570971-b2f8de00-906d-11e9-892e-d3e1c653bdf3.png)

Let's see how this will look in code 

**Create Reminder**
```kotlin
fun createReminder(uid: String, reminderID: String, title: String, description: String, timeStamp: Double) { 

val reminders = currentViewState().reminders 

updateState( 
ReminderViewState( 
progressType = ProgressType.Loading, 
isValidated = currentViewState().isValidated, 
reminders = reminders, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID )
) 

db.reference.child("Reminders").child(uid).child(reminderID).setValue( 
Reminder( 
reminderID, 
title = title, 
description = description, 
isComplete = false, 
timestamp = timeStamp )
).addOnCompleteListener { 
if (it.isSuccessful) { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Result, 
reminders = reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) 
) 
fetchReminders(uid) 
} else { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Failure, 
reminders = currentViewState().reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) ) } } 
}
```

During successfully creating a reminder you can see that we fetch reminders from the database. Fetching is shown below.

### <a name="reading/witingRemindersAndroid"> Reading In A List Of Reminders From The Database </a>

Reading Reminders from the database. Much like how we have been communicating with the database we will with reading reminders from the database. We key into the Reminders then use the users `uid`. After which we can grab and sort our data from the database.

**Fetching Reminders:**
```kotlin
fun fetchReminders(uid: String) {
if (uid.isNotEmpty()) { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Loading, 
isValidated = currentViewState().isValidated, 
reminders = currentViewState().reminders, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) 
) 

db.reference.child("Reminders").child(uid).addListenerForSingleValueEvent( object : ValueEventListener { 

override fun onDataChange(dataSnapshot: DataSnapshot) { 
val data = dataSnapshot.children 
val reminders = mutableListOf<Reminder>() 

for (child in data) { 
val newReminder = child.getValue(Reminder::class.java)

if (newReminder != null) { 
reminders.add(newReminder) 
}
} 

reminders.sortBy { it.timestamp } 

updateState( 
ReminderViewState( 
progressType = ProgressType.Result, 
reminders = reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) 
) 
} 

override fun onCancelled(databaseError: DatabaseError) { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Failure, 
reminders = currentViewState().reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) ) } } ) 
} else { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Failure, 
reminders = currentViewState().reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) ) } 
}
```

**Edit a reminder:**
```kotlin
fun editReminder( uid: String, reminderID: String, title: String, description: String, isComplete: Boolean, timeStamp: Double ) { 

val reminders = currentViewState().reminders 

updateState( ReminderViewState( 
progressType = ProgressType.Loading, 
isValidated = currentViewState().isValidated, 
reminders = reminders, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) 
) 

db.reference.child("Reminders").child(uid).child(reminderID).setValue( 
Reminder( 
reminderID, 
title = title, 
description = description, 
isComplete = isComplete, 
timestamp = timeStamp ) 
).addOnCompleteListener { 
if (it.isSuccessful) { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Result, 
reminders = reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID 
) 
) 

fetchReminders(uid) 
} else { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Failure, 
reminders = currentViewState().reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) ) } } 
}
```

**Delete Reminder:**
```kotlin
fun deleteReminder(uid: String, reminder: Reminder) { 
if (uid.isEmpty()) { 
db.reference.child("Reminders").child(uid).child(reminder.id!!).removeValue().addOnCompleteListener { 
if (it.isSuccessful) { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Result, 
reminders = currentViewState().reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID )
 ) 
fetchReminders(uid) 
} else { 
updateState( 
ReminderViewState( 
progressType = ProgressType.Failure, 
reminders = currentViewState().reminders, 
isValidated = currentViewState().isValidated, 
markedForDeletion = currentViewState().markedForDeletion, 
markedForCompletion = currentViewState().markedForCompletion, 
userUID = currentViewState().userUID ) ) } } } 
}
```


<a name="conclusion"> Conclusion </a>
--------------

To see how all these components all come together I encourage you to explore through the project! You have learned how to authenticate users using Firebase, and edit/write/read data using the FireBase `Realtime Database`. Using these concepts you create and support your own custom applications, and we are excited to see what you come up with; thanks for reading and I hope this helps you and your startup!
