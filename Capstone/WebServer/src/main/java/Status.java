public enum Status {

  SUCCESS("Success"), ERROR("Error");

  final private String status;

  Status(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

}
