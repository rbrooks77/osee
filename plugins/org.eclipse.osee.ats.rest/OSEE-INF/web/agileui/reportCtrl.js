/**
 * Agile Reports Controller
 */
angular
		.module('AgileApp')
		.controller(
				'ReportCtrl',
				[
						'$scope',
						'AgileFactory',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'LayoutService',
						'PopupService',
						function($scope, AgileFactory, $resource, $window,
								$modal, $filter, $routeParams, LayoutService,
								PopupService) {

							$scope.team = {};
							$scope.team.uuid = $routeParams.team;
							$scope.reporttype = $routeParams.reporttype;
							$scope.reportname = $routeParams.reportname;

							$scope.updateReports = function() {
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											AgileFactory
													.getTeamSingle($scope.selectedTeam).$promise
													.then(function(data) {

														$scope.teamName = data.name;
														$scope.team.sprintId = data.sprintId;

														var htmlcontent = $('#b1 ');
														var url = "<object data=\"/ats/agile/team/";
														url = url
																.concat($scope.team.uuid);
														url = url
																.concat("/sprint/");
														url = url
																.concat($scope.team.sprintId);
														if ($scope.reporttype == "burndown") {
															url = url
																	.concat("/burndown/chart/ui?type=best\">");
														} else if ($scope.reporttype == "burnup") {
															url = url
																	.concat("/burnup/chart/ui?type=best\">");
														} else if ($scope.reporttype == "data") {
															url = url
																	.concat("/data/table?type=best\">");
														} else if ($scope.reporttype == "summary") {
															url = url
																	.concat("/summary?type=best\">");
														}

														$("#b1").html(url);

														AgileFactory
																.getSprint(
																		$scope.team.uuid,
																		$scope.team.sprintId).$promise
																.then(function(
																		data) {
																	$scope.reportname = data.name
																			.concat(
																					" - ")
																			.concat(
																					$scope.reportname)
																			.concat(
																					" - ")
																			.concat(
																					new Date()
																							.toLocaleString());
																});
													});
										});
							}

							$scope.updateReports();

						} ]);
