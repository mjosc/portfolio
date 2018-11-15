"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var FamilyGraph = function () {
  function FamilyGraph() {
    _classCallCheck(this, FamilyGraph);

    this.personMap = new Map();
    this.familyMap = new Map();
  }

  _createClass(FamilyGraph, [{
    key: "addPerson",
    value: function addPerson(p) {
      return this.personMap.set(p.id, p);
    }
  }, {
    key: "addFamily",
    value: function addFamily(f) {
      return this.familyMap.set(f.id, f);
    }
  }, {
    key: "getPerson",
    value: function getPerson(id) {
      return this.personMap.get(id);
    }
  }, {
    key: "getFamily",
    value: function getFamily(id) {
      return this.familyMap.get(id);
    }
  }, {
    key: "hasPerson",
    value: function hasPerson(id) {
      return this.personMap.has(id);
    }
  }, {
    key: "hasFamily",
    value: function hasFamily(id) {
      return this.familyMap.has(id);
    }
  }, {
    key: "addChildFamilyEdge",
    value: function addChildFamilyEdge(child, family) {
      family.addChild(child);
      child.addChildFamily(family);
    }
  }, {
    key: "addParentFamilyEdge",
    value: function addParentFamilyEdge(parent, family) {
      family.addParent(parent);
      parent.addParentFamily(family);
    }
  }, {
    key: "childrenInFamily",
    value: function childrenInFamily(f) {
      return f.children;
    }
  }, {
    key: "directLineTraversal",
    value: function directLineTraversal(root) {
      var ancestors = [root];
      this.directLineRecursion(root, ancestors);
      return ancestors;
    }
  }, {
    key: "directLineRecursion",
    value: function directLineRecursion(root, ancestors) {

      if (root.childFamilies.length < 1) {
        return;
      }

      var family = root.childFamilies[0];

      ancestors.push(family.parents[0]);
      ancestors.push(family.parents[1]);

      this.directLineRecursion(family.parents[0], ancestors);
      this.directLineRecursion(family.parents[1], ancestors);
    }
  }]);

  return FamilyGraph;
}();

var Person = function () {
  function Person(id, name) {
    _classCallCheck(this, Person);

    this.id = id;
    this.name = name;
    this.childFamilies = [];
    this.parentFamilies = [];
  }

  _createClass(Person, [{
    key: "addChildFamily",
    value: function addChildFamily(f) {
      this.childFamilies.push(f);
    }
  }, {
    key: "addParentFamily",
    value: function addParentFamily(f) {
      this.parentFamilies.push(f);
    }
  }]);

  return Person;
}();

var Family = function () {
  function Family(id) {
    _classCallCheck(this, Family);

    this.id = id;
    this.parents = [];
    this.children = [];
  }

  _createClass(Family, [{
    key: "addChild",
    value: function addChild(c) {
      this.children.push(c);
    }
  }, {
    key: "addParent",
    value: function addParent(p) {
      this.parents.push(p);
    }
  }]);

  return Family;
}();

exports.FamilyGraph = FamilyGraph;
exports.Person = Person;
exports.Family = Family;