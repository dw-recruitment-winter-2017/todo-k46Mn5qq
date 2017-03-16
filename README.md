## dworks


## Testing
* Phantomjs must be installed for doo runner to work.
  * Linux: sudo apt install phantomjs
* To run doo tests:
  * $ lein doo
    
## Assignment

This assignment is designed to gauge how well you know or can pick up Clojure and some common libraries to build a simple web application.

**We ask that you return this exercise no later than 1 week after receiving it. When prioritizing how to structure your work, you should focus on depth rather than breadth. Do as much good work as you can on each feature you tackle rather than trying to do a little work on more of the features. Your submission will be evaluated on the _quality_ of what you get done, not how many of the features listed below you complete. We _do not_ want you to spend more than 2-4 hours on this, because we respect your time.**

Since we want to evaluate all of these projects anonymously, you should *not* include any identifying data in the code (your name, city, email address, phone number, school, etc.). We regret that we will be unable to give feedback on your exercise until the recruitment process is over.

However, you *may* ask questions of the Democracy Works employee from whom you received the assignment. We ask that you please limit your questions to *only* this person since other members of our team may be evaluating your exercise and should therefore have as little information as possible about which project belongs to you.

You should use git for version control (we'll want the entire git repo submitted). Ideally we're looking for a rough commit-per-feature (as described below) ratio so its easier for us to get some sense of your process via git. In order to keep your commits anonymous, run these git commands **before you commit any code** in your project working directory to change your name and email for this project only:

1. `git init` (if you haven't already)
1. `git config --local user.name 'Anonymous Applicant'`
1. `git config --local user.email anonymous@example.com`

...and make sure you aren't attaching any other identifying information to your commits such as signing them with a GPG key.

Think of this as building an application for a client who will use it and add more features to it over time. What should you deliver to them so that they will be successful at doing both of those things?

## Assignment

Implement a TODO app in Clojure/ClojureScript. The basic architecture is: the backend is Clojure and should handle any state storage (CRUD operations). The front-end is responsible for rendering--that is, we expect you'll send data back and forth vs. rendered HTML to the browser (except for the initial page load which can be a static HTML file that runs the compiled CLJS code).

- The main page at the path "/" should contain the main TODO interface.
- There should be an "/about" page giving a description of the project.
- Per the architecture constraints, all rendering should happen on the client-side using a ClojureScript React wrapper library like Reagent, Rum, or any other of your choosing.
- A TODO consists of two values: completion state (a boolean value) and text description.
- Per the architecture constraints, TODO state should be stored on the backend.

### Features

Remember: Fewer of these done well beats more of them done poorly.

- Add a new TODO (initially incomplete)
- Mark a TODO as completed
- Unmark a TODO as completed (i.e. return it to incomplete state)
- Delete existing TODOs

Don't do any more than this. If you don't get through every bit of this in 2-4 hours, **DO NOT stress it, we are evaluating you on the work you did, not the work you didn't do.**