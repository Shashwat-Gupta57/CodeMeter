class Codemeter < Formula
  desc "Measure your code. Physically."
  homepage "https://github.com/Shashwat-Gupta57/CodeMeter"
  url "https://github.com/Shashwat-Gupta57/CodeMeter/releases/download/v1.0.0/codemeter-macos-amd64"
  version "1.0.0"
  # sha256 will be updated by release script
  sha256 "0000000000000000000000000000000000000000000000000000000000000000"

  def install
    bin.install "codemeter-macos-amd64" => "codemeter"
  end

  test do
    system "#{bin}/codemeter", "--version"
  end
end
