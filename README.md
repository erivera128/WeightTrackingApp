# Computer Science ePortfolio
**Emmanuel Rivera**
B.S. in Computer Science — Southern New Hampshire University

---

## Professional Self-Assessment

After completing my journey as a computer science undergrad, I have gone from zero programming and engineering knowledge to writing functional, secure and proper software. Going through this milestone has really helped me dig into how things can be so different and better but ultimately accomplish the same goal. It taught me that there is a big difference between “student” code and a more professional way to program.

A big part of my learning throughout the program is how important collaboration and your team is to engineering. Learning the agile team structure and code reviews showed me how important communication is between you, your team and any clients/customers. Also, learning that peer reviews show us that bad code does not necessarily mean you failed but as an opportunity to improve in code and engineering. 

As stated before, communication with your team is important but it’s also as important as communicating with the stakeholders. Being able to properly show off your application and how it functions and works for the business is an important skill to learn because it is something that we will rely on as software engineers. Also, knowing how to assess security risks and how it can impact a business, being able to come up with a plan and implement, can show trust to the stakeholders. 

Data structures and algorithms is one of my biggest learned skills. Knowing how to implement working solutions that doesn’t just “work”. As a future engineer, knowing how to analyze and identify ways to have more efficient and quicker algorithms or data structures. As an example, in my artifact I was able to work on how my choice to go between heavy database queries and in memory array allowed to have better processing speed and more efficient processing.

Software development for me was allowing myself to try and think outside the box. To try and think of better frameworks and patterns. As I did in my artifact. I switched my code to Model/View/ViewModel that helps keep code maintainable and for better testing and upgrading. Also, switching to an ORM framework showed off experience between different database environments like relational vs non-relational.

Security has been and will always be a top priority. Even in a simple app that will probably never see the app store, I made sure to implement a more secure algorithm for my login system. After some research, I chose to implement the Bcrypt algorithm with random salt generations. This allowed me to protect sensitive data, like passwords. My coursework throughout my journey at SNHU taught me to always check for errors and potential leaks that can affect users.

I chose to use a single application instead of 3 different artifacts to make enhancements because it allowed me to sort of simulate a complete lifecycle you would see in the real workforce. For my first enhancement, I chose to go away from a single page running basically everything and moved into a more maintainable and upgradable MVVM pattern. My second enhancement I changed from a plain data log into a more helpful system by showing a 7 day trend average. For my third enhancement, I chose to go into a more common day data persistance model with a Room ORM framework as opposed to running raw SQL queries. I believe together, it showcases my skills learned as a future computer scientist.


---

## Informal Code Review Video

* **Code Review:** [Click to view the Code Review](https://youtu.be/P6Kub_M07do)

---

##  Artifact: Weight Tracking Mobile Application

### Enhancement 1: Software Design and Engineering
* **Update:** Model-View-ViewModel (MVVM)

#### Written Narrative
My artifact is a Weight Tracking app using Java and Android Studio. It was made for my CS360 class, Mobile Architecture and Programming. This app lets users create a username, set a weight goal, log daily weight and update weight goal if needed. I chose this project because it shows a complete mobile app and can help demonstrate my abilities as a developer and engineer. 

The specific components of this artifact that show my abilities is mobile architecture. I restructured the app into what is referred  to as MVVM or (Model View ViewModel). This helped showcase a more professional code base that is more maintainable. I improved the artifact by going away from a file or two being what basically handled the entire app. I separated logic into it’s own dedicated ViewModels. This allows for easier testing as well.

I believe this helps meet the course outcomes by showcasing professional quality solutions and best practices. Now, enhancements 2 and 3 will become “easier” to program as my entire codebase is easier to work with. I learned that organization is just as important and good code. The biggest thing that was challenging was making sure nothing was missed as I separated the code. 

---

### Enhancement 2: Algorithms and Data Structures
* **Update:** In-Memory 7-Day Moving Average Traversal Matrix.

#### Written Narrative
The app really had no true algorithms. As the weight was saved, it just pulled values from the database and plainly showed them as is. To add some improvement, I created a utility class. As people who like to track their weight knows, water weight makes your weight fluctuate on the scale day to day. The number you see on the scale may be influenced by many factors that affect your water retention. So I created an algorithm that helps calculate a rolling average to help give you a “trend”. While doing this, I also used a processing loop instead of making a new query every time. This helps reduce processing power.


---

### Enhancement 3: Databases and Security Data Management
* **Update:** Android Room Persistence ORM & BCrypt Cryptographic Hashing.

#### Written Narrative
The enhancements I chose to do were adding security and migrating my database to the Android Room Persistence Library. For security, I used the Bcrypt protocol with hashing filters. Now we avoid storing or exposing sensitive data like passwords for a greater and secure login. For the database, the ORM design used helps demonstrate persistence and lifecycle stability. 

* **Code:** https://github.com/erivera128/WeightTrackingApp
