class FamilyGraph {

  constructor() {
    this.personMap = new Map();
    this.familyMap = new Map();
  }

  addPerson(p) {
    return this.personMap.set(p.id, p);
  }

  addFamily(f) {
    return this.familyMap.set(f.id, f);
  }

  getPerson(id) {
    return this.personMap.get(id);
  }

  getFamily(id) {
    return this.familyMap.get(id);
  }

  hasPerson(id) {
    return this.personMap.has(id);
  }

  hasFamily(id) {
    return this.familyMap.has(id);
  }

  addChildFamilyEdge(child, family) {
    family.addChild(child);
    child.addChildFamily(family);
  }

  addParentFamilyEdge(parent, family) {
    family.addParent(parent);
    parent.addParentFamily(family);
  }

  directLineTraversal(root) {
    let ancestors = [root];
    this.directLineRecursion(root, ancestors);
    return ancestors;
  }

  directLineRecursion(root, ancestors) {
    console.log(root.name);
    if (root.childFamilies.length < 1) {
      // console.log(root.id);
      return;
    }
    // console.log(root.name);
    this.directLineRecursion(root.childFamilies[0].parents[0]);
    this.directLineRecursion(root.childFamilies[0].parents[1]);
  }
}

class Person {
  constructor(id, name) {
    this.id = id;
    this.name = name;
    this.childFamilies = [];
    this.parentFamilies = [];
  }

  addChildFamily(f) {
    this.childFamilies.push(f);
  }

  addParentFamily(f) {
    this.parentFamilies.push(f);
  }
}

class Family {
  constructor(id) {
    this.id = id;
    this.parents = [];
    this.children = [];
  }

  addChild(c) {
    this.children.push(c);
  }

  addParent(p) {
    this.parents.push(p);
  }
}

export {
  FamilyGraph,
  Person,
  Family
}
