# Description

In honor of Detriots startup week we have taking the liberty of implementing a basic app, that aspiring engineers and entrepreneur can use as a source of knowledge and inspiration for their own projects. This project has been accommodated to run on both the Android and iOS platforms, while maintaining a shared custom backend implented via Google Firebase. 

The project we have decided to create is a simple reminder application. Throughout the course of this README we will be explaining how to: implement email and password authentication using Firebase/Auth, reading and writing to a NoSQL database using Firebase/Database, and how to construct relationships between models in JSON.


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
    func putUser(user: User?) {
        guard let user = user,
            let uid = user.uid else {
            return
        }
        
        Database.database().reference().child(Nodes.users.rawValue).child(uid).setValue(user.toDictionary())
    }
```
This function calls into `Database.database().reference()` which is defined in the `GoogleService-Info.plist` file we imported earlier. Next we will key into the `Users` node  by using `.child(Nodes.users.rawValue)` (`Nodes.users.rawValue` is the same as saying `"Users"`), followed by keying into the users unique id node using `.child(uid)` and set the value using our [`Dictionary<String, Any>`](https://www.tutorialspoint.com/swift/swift_dictionaries.htm) representation.

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

Now that we have a way of transforming `DataSnapshot` into a `User` we can now take a look and see how we can leverage `Database.database().observe(of:with:)` and the newly add `DataSnapshot.toUser()`functions to fetch a user from the database.

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
