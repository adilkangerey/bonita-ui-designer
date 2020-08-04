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
(function() {

  'use strict';

  angular
    .module('bonitasoft.designer.editor.palette')
    .directive('paletteModel', paletteModel);

  /**
   * Element directive displaying a widget in the palette, with just its label for now.
   */
  function paletteModel() {
    return {
      restrict: 'EA',
      scope: {
        widget: '='
      },
      templateUrl: 'js/editor/palette/palette-model.html',
      controller: ($scope, $sce, gettextCatalog) =>  {
        $scope.dynamicTooltip = '';
        if ($scope.widget.component) {
          $scope.dynamicTooltip = $scope.widget.component.description ? $sce.trustAsHtml($scope.widget.component.name + '<br/><br/>' + gettextCatalog.getString($scope.widget.component.description)) : $scope.widget.component.name;
        }
      },
    };
  }

})();
