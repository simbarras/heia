FROM mcr.microsoft.com/dotnet/sdk:6.0-alpine AS build
WORKDIR /source

# copy csproj and restore as distinct layers
COPY betApi/*.sln .
COPY betApi/betApi/*.csproj ./betApi/
COPY betApi/betApi.Tests/*.csproj ./betApi.Tests/
RUN dotnet restore

# copy everything else and build app
COPY betApi/betApi/. ./betApi/
COPY betApi/betApi.Tests/. ./betApi.Tests/
# WORKDIR /source/betApi
RUN dotnet publish -c release -o /app --no-restore /restore

# final stage/image
FROM mcr.microsoft.com/dotnet/aspnet:6.0
ENV ASPNETCORE_URLS=http://+:8080
EXPOSE 8080

WORKDIR /app
COPY --from=build /app ./
ENTRYPOINT ["dotnet", "betApi.dll"]