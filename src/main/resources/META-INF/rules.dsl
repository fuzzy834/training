[consequence][]Create journalist user with data from {node}=journalistToUser.createUser({node});
[consequence][]Update user journalist from {node}=journalistToUser.updateAndPublish({node});
[consequence][]Send notification to user email when created {node}=sendNotificationsToUser.onCreate({node});
[consequence][]Send notification to user email when updated {node}=sendNotificationsToUser.onUpdate({node});