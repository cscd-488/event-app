# event!
Check-in at local events, earn badges, and redeem them for real rewards.

### Fork the code
------------------

``` bash
$ git clone https://github.com/
$ cd event-app
$ git remote add upstream https://github.com/
$ git fetch upstream
$ git rebase upstream/master
```

### Contributing
-----------------

``` bash
$ git checkout -b feature_x
    (make changes)
$ git status
$ git add . 
$ git commit -a -m "descriptive commit message for your changes"
```
> The `-b` specifies that you want to create a new branch called `feature_x`.  You only specifiy `-b` the first time you checkout because you are creating a new branch. Once the `feature_x` branch exists you can later switch to it with only `git checkout feature_x`.

### Rebase `feature_x` against upstream/master
-----------------------------------------------

``` bash
$ git checkout master
$ git fetch upstream
$ git rebase upstream/master
$ git checkout feature_x
$ git rebase master
```

> Now your `feature_x` is up-to-date with the upstream code.


### Submit a PR
---------------

> **IMPORTANT:** Make sure you have rebased your `feature_x` branch to include the latest code from `upstream/master` before you do this.

``` bash
$ git push origin master
$ git push origin feature_x
```

Now that `feature_x` is up to date, and has been pushed to your fork, you can initiate the pull request.
To initiate the pull request, do the following:

1. In your browser, navigate to your forked repository
2. Click the new button called '**Compare & pull request**' that showed up just above the main area in your forked repository
3. Validate the pull request will be into the upstream `master` branch and will be from your `feature_x` branch
4. Enter a detailed description of the work you have done and then click '**Send pull request**'

### Cleaning up after a successful PR
--------------------------------------

Once the `feature_x` branch has been commited into the `upstream/master` branch, your local `feature_x` branch and the `origin/feature` branch are no longer needed.  If you want to make additional changes, restart the process with a new branch.

> **IMPORTANT:** Make sure that your changes are in `upstream/master` before you delete your `feature_x` and `origin/feature_x` branches!

``` bash
$ git checkout master
$ git branch -D feature_x
$ git push origin :feature_x
```
