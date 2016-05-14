var app = angular.module('myApp', []);
app.controller('FormController', ['$scope', '$http', function ($scope, $http) {


    this.ces = {
        "id": '',
        "year": '',
        "startRegistrationDate": '',
        "endRegistrationDate": '',
        "startInterviewingDate": '',
        "endInterviewingDate": '',
        "quota": '',
        "reminders": '',
        "interviewTimeForPerson": '',
        "interviewTimeForDay": '',
        "statusId": ''
    };
    $scope.current = false;
    $scope.interviewBegan = false;
    $http.get('/cessettings').success(function(response) {
        if (response.statusId == 1){
            $scope.current = true;
        } else if (response.statusId == 4 ){
            $scope.current = true;
            $scope.interviewBegan = true;
        }
        console.log(response);
        $scope.ctrl.ces.year =  response.year;
        $scope.ctrl.ces.id = response.id;
        $scope.ctrl.ces.statusId = response.statusId;
        $scope.ctrl.ces.startRegistrationDate = new Date(response.startRegistrationDate);
        $scope.ctrl.ces.endRegistrationDate = new Date(response.endRegistrationDate);
        $scope.ctrl.ces.startInterviewingDate = new Date(response.startInterviewingDate);
        $scope.ctrl.ces.endInterviewingDate = new Date(response.endInterviewingDate);
        $scope.ctrl.ces.quota = response.quota;
        $scope.ctrl.ces.reminders = response.reminders;
        $scope.ctrl.ces.interviewTimeForDay = response.interviewTimeForDay;
        $scope.ctrl.ces.interviewTimeForPerson = response.interviewTimeForPerson;
    }).error(function(){
        $scope.postError = true;
    });
    this.save = function () {
        if (!$scope.current) {
            if (new Date() > new Date(this.ces.startRegistrationDate)) {
                alert("choose another start registration day!");
                return
            }
            if (new Date(this.ces.startRegistrationDate) >= new Date(this.ces.endRegistrationDate)) {
                alert("Start registration date >= end registration date ");
                return
            }
        }

        if ((this.ces.startInterviewingDate != ' ') && (!$scope.interviewBegan)) {
            if (new Date(this.ces.startInterviewingDate) <= new Date()){
                alert("choose another start interviewing day!");
                return
            }
            if (new Date(this.ces.startInterviewingDate) >= new Date(this.ces.endInterviewingDate)) {
                alert("Start interviewing date >= end interviewing date ");
                return
            }
            if (new Date(this.ces.startRegistrationDate) >= new Date(this.ces.startInterviewingDate)) {
                alert("Start registration date >= start interviewing date ");
                return
            }
            if (new Date(this.ces.startInterviewingDate) <= new Date(this.ces.endRegistrationDate)) {
                alert("Start interviewing date <= end registration date ");
                return
            }
        }
        if (new Date() < new Date(this.ces.startRegistrationDate)){
            this.ces.statusId = '1';
        } else if (new Date() > new Date(this.ces.endInterviewingDate)){
            this.ces.statusId = '3';
        } else {
            this.ces.statusId = '2';
        }
        $http.post('/cesPost', this.ces).success(function() {
            $scope.postSuccess = true
        }).error(function(){
            $scope.postError = true;
        });
    };
}]);