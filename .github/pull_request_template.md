## [Jira ticket link](link here)

Short description if any.

Related PRs:
- If any

Author Todo List:

- [ ] Add/adjust tests (if applicable)
- [ ] Build in CI passes
- [ ] Latest master revision is merged into the branch
- [ ] Self-Review
- [ ] Add reviewers

Reviewer Todo List:

- The PR and the branch names describe the change
- The PR is branch -> master
- The change addresses the described issue
- The change follows defined [code style](https://liveintent.atlassian.net/wiki/spaces/EB/pages/827064330/Scala+Code+Style)
- The change provides necessary test adjustments
- The public API documentation is updated (for public facing projects)
- The config variables naming is correct
- There are no Java/Scala specific issues:
  - Future creation can only return failed future, doesn't throw exceptions
  - No resources are left open
  - Try is not silencing thrown exceptions

If reviewer requests major changes they remove `Ready for Review` label until the comments are addressed.
