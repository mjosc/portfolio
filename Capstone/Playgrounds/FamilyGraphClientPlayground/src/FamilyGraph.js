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

  childrenInFamily(f) {
    return f.children;
  }

  directLineTraversal(root) {
    let ancestors = [root];
    this.directLineRecursion(root, ancestors);
    return ancestors;
  }

  directLineRecursion(root, ancestors) {
  
    if (root.childFamilies.length < 1) {
      return;
    }
    
    let family = root.childFamilies[0];

    ancestors.push(family.parents[0]);
    ancestors.push(family.parents[1]);

    this.directLineRecursion(family.parents[0], ancestors);
    this.directLineRecursion(family.parents[1], ancestors);
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
