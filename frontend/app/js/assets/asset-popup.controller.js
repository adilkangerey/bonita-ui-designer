/**
 * Copyright (C) 2015 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
(function () {

  angular.module('bonitasoft.ui.assets').controller('AssetPopupCtrl', function ($scope, $modalInstance, alerts, assetsService, asset, mode, artifact) {

    'use strict';

    var urlPrefixForLocalAsset = 'rest/' + mode + 's/' + artifact.id + '/assets/';

    $scope.asset = asset;
    $scope.isNewAsset = asset === undefined;

    //All datas (type, sources) are defined in the assets service.
    $scope.assetType = assetsService.getType();
    $scope.assetSource = assetsService.getSource();

    //Asset is converted in another object for the html form
    $scope.newAsset = assetsService.assetToForm(asset);

    $scope.cancel = cancel;
    $scope.isExternalAsset = assetsService.isExternal;
    $scope.onSuccess = onSuccess;
    $scope.onError = onError;
    $scope.saveExternalAsset = saveExternalAsset;
    $scope.updateSavingAction = updateSavingAction;
    $scope.assetSavingAction = urlPrefixForLocalAsset + 'js';

    /**
     * An external asset is saved by a $http call
     */
    function saveExternalAsset(data) {
      data.isNew = $scope.isNewAsset;
      $modalInstance.close(data);
    }

    /**
     * A local asset (file) is saved by the submit of the html form
     */
    function onSuccess(response) {
      //Even if a problem occurs in the backend a response is sent with a message
      //If the message has a type and a message this an error
      if (response && response.type && response.message) {
        alerts.addError(response.message);
      }
      $modalInstance.close();
    }

    /**
     * If an error occurred on saving, the message is displayed in alerts area
     */
    function onError(error) {
      alerts.addError(error.message);
      $modalInstance.dismiss('cancel');
    }

    /**
     * User clicked on Cancel button
     */
    function cancel() {
      $modalInstance.dismiss();
    }

    /**
     * The form action target is not the same according to the asset type : css, js or img
     */
    function updateSavingAction(type) {
      $scope.assetSavingAction = urlPrefixForLocalAsset + type;
    }
  });

})();
