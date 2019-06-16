# Description

In honor of Detriots startup week we have taking the liberty of implementing a basic app, that aspiring engineers and entrepreneur can use as a source of knowledge and inspiration for their own projects. This project has been accommodated to run on both the Android and iOS platforms, while maintaining a shared custom backend implented via Google Firebase. 

The project we have decided to create is a simple reminder application. Throughout the course of this README we will be explaining how to: implement email and password authentication using Firebase/Auth, reading and writing to a NoSQL database using Firebase/Database, and how to construct relationships between models in JSON.

# iOS

## Contents

1. [Creating A New XCode Project](#newProject) 
2. [Installing Firebase Using CocoaPods](#cocoapods)


<a name="newProject"> Creating A New Xcode Project </a>
--------------
If you don't already have Xcode installed on your computer you can install directly from the Mac AppStore. Once you open the application you will be promted with the following screen.

<img width="500" alt="Screen Shot 2019-06-15 at 8 39 48 PM" src="https://user-images.githubusercontent.com/22037563/59557675-d9574480-8fad-11e9-8943-cce73fac8c53.png">

Go ahead and click `Create a new Xcode project` and select the `Single view app` is selected under the `Application` section. You will then be prompted to name your application, and perhaps for an organization name and/or identifer. You can use anything in pretty anything you want for these fields. Tap next and congratulations you just created an Xcode project!

<a name="cocoapods"> Installing Firebase Using Cocoapods </a>
--------------

You may be asking yourself what is CocoaPods? CocoaPods is a dependency manager for Swift and Objective-C projects. A dependency manager is a tool that allows to import external software libraries into your project; if your a curious to learn more about what CocoaPods is see [here](https://www.youtube.com/watch?v=7nojXzgrlNU).  

To install CocoaPods open `Terminal` and type the following command:
```
sudo gem install cocoapods
```
If you are having trouble installing CocoaPods see [here](https://stackoverflow.com/questions/20755044/how-to-install-cocoapods) for potential troubleshooting solutions.

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



